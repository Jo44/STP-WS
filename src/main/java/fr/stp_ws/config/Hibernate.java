package fr.stp_ws.config;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.resource.transaction.spi.TransactionStatus;

import fr.stp_ws.domain.exception.TechnicalException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.PersistenceException;

/**
 * Hibernate
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class Hibernate {

	private static final Logger LOGGER = LogManager.getLogger(Hibernate.class);
	private SessionFactory factory;
	private StandardServiceRegistry registry;
	private final String smtpHostname;
	private final int smtpPort;
	private final boolean smtpSsl;
	private final String smtpUser;
	private final String smtpPass;
	private final String dbUrl;
	private final String dbUsername;
	private final String dbPassword;
	private final String emailTarget;
	private final String emailObject;
	private final String emailContent;
	private final int maxRetryNb = 5;
	private int retryNb = 0;

	/**
	 * Constructor
	 *
	 * @param settings
	 */
	@Inject
	public Hibernate(Settings settings) {
		// Load parameters
		smtpHostname = settings.getString("smtp.hostname");
		smtpPort = settings.getInt("smtp.port");
		smtpSsl = settings.getBoolean("smtp.ssl");
		smtpUser = settings.getString("smtp.user");
		smtpPass = settings.getString("smtp.pass");
		dbUrl = settings.getString("db.url");
		dbUsername = settings.getString("db.username");
		dbPassword = settings.getString("db.password");
		emailTarget = settings.getString("email.target");
		emailObject = settings.getString("email.object");
		emailContent = settings.getString("email.content");
		// Initialize Hibernate
		initialize();
	}

	/** Initialize */
	private void initialize() {
		try {
			LOGGER.info("Hibernate initialization ...");
			// Load 'hibernate.cfg.xml'
			StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
					.configure("hibernate.cfg.xml");
			applySettingIfPresent(registryBuilder, "hibernate.connection.url", dbUrl);
			applySettingIfPresent(registryBuilder, "hibernate.connection.username", dbUsername);
			applySettingIfPresent(registryBuilder, "hibernate.connection.password", dbPassword);
			registry = registryBuilder.build();
			MetadataSources sources = new MetadataSources(registry);
			Metadata metadata = sources.getMetadataBuilder().build();
			factory = metadata.getSessionFactoryBuilder().build();
			LOGGER.info("Hibernate initialized.");
		} catch (Exception ex) {
			LOGGER.error("Error while Hibernate initialization : {}", ex.getMessage());
			if (registry != null) {
				StandardServiceRegistryBuilder.destroy(registry);
			}
		}
	}

	private void applySettingIfPresent(StandardServiceRegistryBuilder registryBuilder, String key, String value) {
		if (value != null && !value.isBlank()) {
			registryBuilder.applySetting(key, value);
		}
	}

	/** Shutdown */
	public void shutdown() {
		if (factory != null && !factory.isClosed()) {
			factory.close();
		}
		if (registry != null) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
		LOGGER.info("Hibernate shutdowned.");
	}

	/**
	 * Get a "ready-to-use" session (with an active transaction)
	 *
	 * @return Session
	 * @throws TechnicalException
	 */
	public Session openSession() throws TechnicalException {
		Session session = null;
		try {
			// Open a new session
			session = factory.openSession();
			LOGGER.debug("Session opened ..");
			// Begin transaction
			session.beginTransaction();
		} catch (Exception ex) {
			retryNb++;
			LOGGER.error("Error #{} while beginning transaction : {}", retryNb, ex.getMessage());
			// Retry to open a new session
			session = openSessionRetry(session);
		}
		// Return exception if the transaction is still not active
		if (session == null || session.getTransaction() == null
				|| session.getTransaction().getStatus() != TransactionStatus.ACTIVE) {
			throw new TechnicalException("Unable to begin transaction !");
		}
		LOGGER.debug("Transaction begun.");
		retryNb = 0;
		return session;
	}

	/**
	 * Refresh Hibernate and retry to open a new session
	 *
	 * @param session
	 * @return Session
	 * @throws TechnicalException
	 */
	private Session openSessionRetry(Session session) throws TechnicalException {
		if (retryNb < maxRetryNb) {
			LOGGER.debug("Retrying to open a new session ...");
			// Close the current session
			session.close();
			// Reinitialize Hibernate
			initialize();
			// Open a new session
			session = openSession();
		} else {
			// Send an email to the administrator
			sendEmail(emailTarget, emailObject, emailContent);
			throw new TechnicalException("Unable to open a new session after " + maxRetryNb + " attempts. Aborted !");
		}
		return session;
	}

	/**
	 * Commit the transaction and close the session
	 *
	 * @param session
	 * @throws PersistenceException
	 */
	public void commit(Session session) throws PersistenceException {
		if (session != null && session.isOpen() && session.getTransaction() != null) {
			try (session) {
				session.flush();
				session.getTransaction().commit();
				LOGGER.debug("Transaction commited.");
			} catch (PersistenceException pex) {
				LOGGER.error("Transaction roll-backed.");
				throw pex;
			} finally {
				session.close();
				LOGGER.debug("Hibernate session closed.");
			}
		}
	}

	/**
	 * Roll-back the transaction and close the session
	 *
	 * @param session
	 * @throws PersistenceException
	 */
	public void rollback(Session session) throws PersistenceException {
		if (session != null && session.isOpen()) {
			try (session) {
				if (session.getTransaction() != null) {
					session.getTransaction().rollback();
					LOGGER.debug("Transaction roll-backed.");
				}
			} catch (PersistenceException pex) {
				LOGGER.error("Transaction roll-backed.");
				throw pex;
			} finally {
				session.close();
				LOGGER.debug("Hibernate session closed.");
			}
		}
	}

	/**
	 * Hibernate connection test
	 *
	 * @return boolean
	 */
	public boolean testConnection() {
		LOGGER.info("Hibernate connection test :");
		boolean ready = false;
		try (Session session = factory.openSession()) {
			if (session != null && session.isConnected()) {
				session.beginTransaction();
				session.getTransaction().commit();
				session.close();
				ready = true;
				LOGGER.info("Hibernate connection test successful !");
			}
		} catch (Exception ex) {
			LOGGER.error("Error while testing Hibernate connection : {}", ex.getMessage());
		}
		return ready;
	}

	/**
	 * Send an email to the administrator
	 *
	 * @param target
	 * @param subject
	 * @param message
	 */
	private void sendEmail(String target, String subject, String message) {
		try {
			Email email = new SimpleEmail();
			email.setHostName(smtpHostname);
			email.setSmtpPort(smtpPort);
			email.setSSLOnConnect(smtpSsl);
			email.setAuthenticator(new DefaultAuthenticator(smtpUser, smtpPass));
			email.setSSLOnConnect(true);
			email.setFrom(smtpUser);
			email.setSubject(subject);
			email.setMsg(message);
			email.addTo(target);
			email.send();
			LOGGER.info("Email sent to the administrator : {}", target);
		} catch (EmailException eex) {
			LOGGER.error("Error while sending email to the administrator : {}", eex.getMessage());
		}
	}
}
