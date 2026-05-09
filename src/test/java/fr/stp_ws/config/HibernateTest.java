package fr.stp_ws.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.Session;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.TechnicalException;
import jakarta.persistence.PersistenceException;

/**
 * Hibernate configuration tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Hibernate tests")
class HibernateTest {

	private Settings settings;
	private Hibernate hibernate;

	/** Before each test */
	@BeforeEach
	void setUp() {
		Settings testSettings = new Settings();
		// Settings mock
		settings = mock(Settings.class);
		when(settings.getString("smtp.hostname")).thenReturn("smtp.test.com");
		when(settings.getInt("smtp.port")).thenReturn(587);
		when(settings.getBoolean("smtp.ssl")).thenReturn(true);
		when(settings.getString("smtp.user")).thenReturn("test@test.com");
		when(settings.getString("smtp.pass")).thenReturn("password");
		when(settings.getString("db.url")).thenReturn(testSettings.getString("db.url"));
		when(settings.getString("db.username")).thenReturn(testSettings.getString("db.username"));
		when(settings.getString("db.password")).thenReturn(testSettings.getString("db.password"));
		when(settings.getString("email.target")).thenReturn("admin@test.com");
		when(settings.getString("email.object")).thenReturn("Test Object");
		when(settings.getString("email.content")).thenReturn("Test Content");
		// Hibernate initialization
		hibernate = new Hibernate(settings);
	}

	/** After each test */
	@AfterEach
	void tearDown() {
		if (hibernate != null) {
			// Hibernate shutdown
			hibernate.shutdown();
		}
	}

	/** Session management tests */
	@Nested
	@DisplayName("Session management tests")
	class SessionManagementTests {

		@Test
		@DisplayName("Should open a session successfully")
		void shouldOpenSessionSuccessfully() throws TechnicalException {
			// When
			Session session = hibernate.openSession();
			// Then
			assertNotNull(session);
			assertTrue(session.isOpen());
			assertEquals(TransactionStatus.ACTIVE, session.getTransaction().getStatus());
			// Cleanup
			hibernate.rollback(session);
		}

		@Test
		@DisplayName("Should commit a session successfully")
		void shouldValidateSessionSuccessfully() throws TechnicalException {
			// Given
			Session session = hibernate.openSession();
			// When
			hibernate.commit(session);
			// Then
			assertFalse(session.isOpen());
		}

		@Test
		@DisplayName("Should rollback a session successfully")
		void shouldCloseSessionSuccessfully() throws TechnicalException {
			// Given
			Session session = hibernate.openSession();
			// When
			hibernate.rollback(session);
			// Then
			assertFalse(session.isOpen());
		}
	}

	/** Connection tests */
	@Nested
	@DisplayName("Connection tests")
	class ConnectionTests {

		@Test
		@DisplayName("Should test the connection successfully")
		void shouldTestConnectionSuccessfully() {
			// When
			boolean isConnected = hibernate.testConnection();
			// Then
			assertTrue(isConnected);
		}
	}

	/** Error handling tests */
	@Nested
	@DisplayName("Error handling tests")
	class ErrorHandlingTests {

		@Test
		@DisplayName("Should handle a persistence error during commit")
		void shouldHandlePersistenceExceptionDuringValidation() {
			// Given
			User user = new User();
			user.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
			user.setEmail("test@example.com");
			// When & Then
			PersistenceException pex = assertThrows(PersistenceException.class, () -> {
				Session session = hibernate.openSession();
				session.persist(user);
				session.flush();
			});
			assertNotNull(pex.getMessage());
		}

		@Test
		@DisplayName("Should handle a persistence error during rollback")
		void shouldHandlePersistenceExceptionDuringRollback() {
			// Given
			User user = new User();
			user.setCreationDate(Timestamp.valueOf(LocalDateTime.now()));
			user.setEmail("test@example.com");
			// When & Then
			PersistenceException pex = assertThrows(PersistenceException.class, () -> {
				try (Session session = hibernate.openSession()) {
					session.persist(user);
				}
			});
			assertNotNull(pex.getMessage());
		}
	}
}
