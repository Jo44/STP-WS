package fr.stp_ws.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Settings tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Settings tests")
class SettingsTest {

	private Settings settings;

	/** Before each test */
	@BeforeEach
	void setUp() {
		settings = new Settings();
	}

	/** String retrieval tests */
	@Nested
	@DisplayName("String retrieval tests")
	class StringSettingsTests {

		@Test
		@DisplayName("Should retrieve an existing string")
		void shouldGetExistingString() {
			// Given
			String key = "smtp.hostname";
			// When
			String value = settings.getString(key);
			// Then
			assertEquals("smtp.yopmail.com", value);
		}

		@Test
		@DisplayName("Should return null for a non-existent key")
		void shouldReturnNullForNonExistentKey() {
			// Given
			String key = "non.existent.key";
			// When
			String value = settings.getString(key);
			// Then
			assertNull(value);
		}
	}

	/** Integer retrieval tests */
	@Nested
	@DisplayName("Integer retrieval tests")
	class IntegerSettingsTests {

		@Test
		@DisplayName("Should retrieve an existing integer")
		void shouldGetExistingInteger() {
			// Given
			String key = "smtp.port";
			// When
			int value = settings.getInt(key);
			// Then
			assertEquals(587, value);
		}

		@Test
		@DisplayName("Should throw an exception for a non-existent key")
		void shouldThrowExceptionForNonExistentKey() {
			// Given
			String key = "non.existent.key";
			// When & Then
			NumberFormatException nfex = assertThrows(NumberFormatException.class, () -> settings.getInt(key));
			assertNotNull(nfex.getMessage());
		}

		@Test
		@DisplayName("Should throw an exception for a non-numeric value")
		void shouldThrowExceptionForNonNumericValue() {
			// Given
			String key = "smtp.hostname";
			// When & Then
			NumberFormatException nfex = assertThrows(NumberFormatException.class, () -> settings.getInt(key));
			assertNotNull(nfex.getMessage());
		}
	}

	/** Boolean retrieval tests */
	@Nested
	@DisplayName("Boolean retrieval tests")
	class BooleanSettingsTests {

		@Test
		@DisplayName("Should retrieve a true boolean")
		void shouldGetTrueBoolean() {
			// Given
			String key = "smtp.ssl";
			// When
			boolean value = settings.getBoolean(key);
			// Then
			assertTrue(value);
		}

		@Test
		@DisplayName("Should return false for a non-existent key")
		void shouldReturnFalseForNonExistentKey() {
			// Given
			String key = "non.existent.key";
			// When
			boolean value = settings.getBoolean(key);
			// Then
			assertFalse(value);
		}

		@Test
		@DisplayName("Should return false for a non-boolean value")
		void shouldReturnFalseForNonBooleanValue() {
			// Given
			String key = "smtp.hostname";
			// When
			boolean value = settings.getBoolean(key);
			// Then
			assertFalse(value);
		}
	}
}
