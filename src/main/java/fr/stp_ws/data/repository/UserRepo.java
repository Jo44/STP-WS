package fr.stp_ws.data.repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.util.Utils;
import com.google.api.client.json.gson.GsonFactory;

import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyExistUserException;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.InvalidTokenException;
import fr.stp_ws.domain.exception.NotExistUserException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.exception.UnrecognizedUserException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;

/**
 * User repository implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class UserRepo implements IUserRepo {

	private static final Logger LOGGER = LogManager.getLogger(UserRepo.class);
	private final Hibernate hibernate;
	private final String googleClient;
	private final String getFromGoogleId;
	private final String getFromEmailPassword;
	private final String getFromEmail;
	private final String getFromRefreshToken;

	/**
	 * Constructor
	 *
	 * @param hibernate
	 * @param settings
	 */
	@Inject
	public UserRepo(Hibernate hibernate, Settings settings) {
		this.hibernate = hibernate;
		// Load SQL requests from settings
		googleClient = settings.getString("auth.google.client");
		getFromGoogleId = settings.getString("sql.user.get.from.google.id");
		getFromEmailPassword = settings.getString("sql.user.get.from.email.password");
		getFromEmail = settings.getString("sql.user.get.from.email");
		getFromRefreshToken = settings.getString("sql.user.get.from.refresh.token");
	}

	/* User - Get */

	/**
	 * Get a user by ID
	 *
	 * @param userId
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public User getById(Integer userId) throws FunctionalException, TechnicalException {
		User user = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve user
			user = session.find(User.class, userId);
			if (user == null) {
				throw new UnrecognizedUserException("Unrecognized user");
			}
			// Commit
			hibernate.commit(session);
			LOGGER.debug("User retrieved successfully (by ID)");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by ID) : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by ID) : {}", pex.getMessage());
			throw new TechnicalException("Unable to get user (by ID)");
		}
		return user;
	}

	/**
	 * Get a user by credentials
	 *
	 * @param email
	 * @param password
	 * @param googleToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public User getByCredentials(String email, String password, String googleToken)
			throws FunctionalException, TechnicalException {
		User user = null;
		// Check if the user is trying to authenticate with email / password or Google
		// token (OAuth 2.0)
		if (googleToken == null) {
			// Via email / password
			user = getByEmailAndPassword(email, password);
		} else {
			// Via Google token (OAuth 2.0)
			user = getByGoogleToken(googleToken);
		}
		return user;
	}

	/**
	 * Get a user by email and password
	 *
	 * @param email
	 * @param password
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public User getByEmailAndPassword(String email, String password) throws FunctionalException, TechnicalException {
		User user = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve user
			Query<User> query = session.createQuery(getFromEmailPassword, User.class);
			query.setParameter("email", email);
			query.setParameter("password", password);
			try {
				user = query.getSingleResult();
			} catch (NoResultException nrex) {
				throw new UnrecognizedUserException("Unrecognized user");
			}
			// Create refresh token
			user.setRefreshToken(UUID.randomUUID().toString());
			// Update refresh token expiry date (+ 1 month)
			user.setRefreshTokenExpiry(Timestamp.valueOf(LocalDate.now().plusMonths(1).atStartOfDay()));
			// Update user last update date
			user.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// Update user
			session.merge(user);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("User retrieved successfully (by email / password)");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by email / password) : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by email / password) : {}", pex.getMessage());
			throw new TechnicalException("Unable to get user (by email / password)");
		}
		return user;
	}

	/**
	 * Get a user by Google token
	 *
	 * @param googleToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public User getByGoogleToken(String googleToken) throws FunctionalException, TechnicalException {
		User user = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve Google ID token
			GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(Utils.getDefaultTransport(),
					GsonFactory.getDefaultInstance()).setAudience(Collections.singletonList(googleClient)).build();
			GoogleIdToken googleIDToken = verifier.verify(googleToken);
			if (googleIDToken != null) {
				// Retrieve payload from Google ID token
				Payload payload = googleIDToken.getPayload();
				String googleID = payload.getSubject();
				String email = payload.getEmail();
				String name = (String) payload.get("name");
				// Retrieve user
				Query<User> query = session.createQuery(getFromGoogleId, User.class);
				query.setParameter("googleID", googleID);
				try {
					user = query.getSingleResult();
					if (user == null) {
						throw new NoResultException("Unrecognized user");
					}
				} catch (NoResultException nrex) {
					// Create user if unrecognized
					user = new User(null, name, email, false, true, googleID, null, null,
							new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
							new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
					session.persist(user);
					LOGGER.debug("User added successfully (by Google token)");
				}
				// Create refresh token
				user.setRefreshToken(UUID.randomUUID().toString());
				// Update refresh token expiry date (+ 1 month)
				user.setRefreshTokenExpiry(Timestamp.valueOf(LocalDate.now().plusMonths(1).atStartOfDay()));
				// Update user last update date
				user.setLastUpdate(new Timestamp(System.currentTimeMillis()));
				// Update user
				session.merge(user);
			} else {
				throw new InvalidTokenException("Invalid Google token");
			}
			// Commit
			hibernate.commit(session);
			LOGGER.debug("User retrieved successfully (by Google token)");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by Google token) : {}", fex.getMessage());
			throw fex;
		} catch (Exception ex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by Google token) : {}", ex.getMessage());
			throw new TechnicalException("Unable to get user (by Google token)");
		}
		return user;
	}

	/**
	 * Get a user by refresh token
	 *
	 * @param refreshToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public User getByRefreshToken(String refreshToken) throws FunctionalException, TechnicalException {
		User user = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve user
			Query<User> query = session.createQuery(getFromRefreshToken, User.class);
			query.setParameter("refreshToken", refreshToken);
			try {
				user = query.getSingleResult();
			} catch (NoResultException nrex) {
				throw new UnrecognizedUserException("Unrecognized user");
			}
			// Update refresh token expiry date (+ 1 month)
			user.setRefreshTokenExpiry(Timestamp.valueOf(LocalDate.now().plusMonths(1).atStartOfDay()));
			// Update user last update date
			user.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// Update user
			session.merge(user);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("User retrieved successfully (by refresh token)");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by refresh token) : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving user (by refresh token) : {}", pex.getMessage());
			throw new TechnicalException("Unable to get user (by refresh token)");
		}
		return user;
	}

	/* User - Add / Delete */

	/**
	 * Add a user
	 *
	 * @param name
	 * @param email
	 * @param password
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public User add(String name, String email, String password) throws FunctionalException, TechnicalException {
		User user = null;
		Session session = hibernate.openSession();
		try {
			// Check if the email is already used by another user
			Query<User> query = session.createQuery(getFromEmail, User.class);
			query.setParameter("email", email);
			query.setParameter("google", false);
			try {
				query.getSingleResult();
				throw new AlreadyExistUserException("Email already exists");
			} catch (NoResultException nrex) {
				// Create user
				user = new User(null, name, email, false, false, password, UUID.randomUUID().toString(),
						Timestamp.valueOf(LocalDate.now().plusMonths(1).atStartOfDay()),
						new Timestamp(System.currentTimeMillis()), new Timestamp(System.currentTimeMillis()),
						new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
				session.persist(user);
			}
			// Commit
			hibernate.commit(session);
			LOGGER.debug("User added successfully (by email / password)");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding user : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding user : {}", pex.getMessage());
			throw new TechnicalException("Unable to add user");
		}
		return user;
	}

	/**
	 * Update a user
	 *
	 * @param refreshToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public User update(String refreshToken) throws FunctionalException, TechnicalException {
		User user = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve user
			Query<User> query = session.createQuery(getFromRefreshToken, User.class);
			query.setParameter("refreshToken", refreshToken);
			try {
				user = query.getSingleResult();
			} catch (NoResultException nrex) {
				throw new NotExistUserException("User does not exist");
			}
			// Delete refresh token
			user.setRefreshToken(null);
			// Delete refresh token expiry date
			user.setRefreshTokenExpiry(null);
			// Update user last update date
			user.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// Update user
			session.merge(user);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("User updated successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while updating user : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while updating user : {}", pex.getMessage());
			throw new TechnicalException("Unable to update user");
		}
		return user;
	}
}
