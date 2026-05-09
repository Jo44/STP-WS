package fr.stp_ws.config;

import java.io.IOException;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.inject.Singleton;

/**
 * Settings
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class Settings {

	private static final Logger LOGGER = LogManager.getLogger(Settings.class);
	private final Properties properties;

	/** Constructor */
	public Settings() {
		properties = new Properties();
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			// Load 'settings.properties'
			properties.load(classLoader.getResourceAsStream("settings.properties"));
		} catch (IOException ioex) {
			LOGGER.error(ioex.getMessage());
		}
	}

	/* Getters */

	public String getString(String key) {
		return properties.getProperty(key, null);
	}

	public int getInt(String key) {
		String parametreStr = properties.getProperty(key, null);
		return Integer.parseInt(parametreStr);
	}

	public boolean getBoolean(String key) {
		String parametreStr = properties.getProperty(key, null);
		return Boolean.parseBoolean(parametreStr);
	}
}
