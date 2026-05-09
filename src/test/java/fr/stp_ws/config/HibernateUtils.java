package fr.stp_ws.config;

import fr.stp_ws.domain.exception.TechnicalException;

/**
 * Hibernate utilities
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class HibernateUtils {

	private static Settings settings;
	private static Hibernate hibernate;
	private static boolean initialized = false;

	/** Constructor */
	private HibernateUtils() {
		// Private constructor to prevent instantiation
	}

	/**
	 * Initialize the Settings and Hibernate instances if needed
	 *
	 * @throws TechnicalException
	 */
	public static synchronized void initialize() throws TechnicalException {
		if (!initialized) {
			settings = new Settings();
			hibernate = new Hibernate(settings);
			initialized = true;
		}
	}

	/**
	 * Retrieve the Settings instance
	 *
	 * @return Settings
	 * @throws TechnicalException
	 */
	public static Settings getSettings() throws TechnicalException {
		if (!initialized) {
			initialize();
		}
		return settings;
	}

	/**
	 * Retrieve the Hibernate instance
	 *
	 * @return Hibernate
	 * @throws TechnicalException
	 */
	public static Hibernate getHibernate() throws TechnicalException {
		if (!initialized) {
			initialize();
		}
		return hibernate;
	}

	/** Properly shuts down Hibernate */
	public static void shutdown() {
		if (initialized && hibernate != null) {
			hibernate.shutdown();
			initialized = false;
			hibernate = null;
			settings = null;
		}
	}
}
