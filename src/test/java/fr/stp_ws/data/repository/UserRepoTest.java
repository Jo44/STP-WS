package fr.stp_ws.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.HibernateUtils;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyExistUserException;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistUserException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.exception.UnrecognizedUserException;
import jakarta.persistence.PersistenceException;

/**
 * User repository tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("User repository tests")
class UserRepoTest {

	private static Settings settings;
	private static Hibernate hibernate;
	private UserRepo userRepo;
	private User testUser;

	/** Before all tests */
	@BeforeAll
	static void setUpClass() throws TechnicalException {
		// Initialize settings and Hibernate
		HibernateUtils.initialize();
		settings = HibernateUtils.getSettings();
		hibernate = HibernateUtils.getHibernate();
	}

	/** After all tests */
	@AfterAll
	static void tearDownClass() {
		// Shut down Hibernate
		HibernateUtils.shutdown();
	}

	/** Before each test */
	@BeforeEach
	void setUp() throws TechnicalException {
		// User repository
		userRepo = new UserRepo(hibernate, settings);
		// Create a user
		testUser = new User();
		testUser.setName("Test User");
		testUser.setEmail("test@example.com");
		testUser.setTourist(false);
		testUser.setGoogle(false);
		testUser.setSecret("test_secret");
		testUser.setRefreshToken(UUID.randomUUID().toString());
		testUser.setRefreshTokenExpiry(Timestamp.valueOf(LocalDate.now().plusMonths(1).atStartOfDay()));
		testUser.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testUser.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		testUser.setPlaces(new ArrayList<>());
		testUser.setPlacelists(new ArrayList<>());
		testUser.setComments(new ArrayList<>());
		// Persist test entities
		var session = hibernate.openSession();
		try {
			session.persist(testUser);
			session.flush();
			hibernate.commit(session);
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			throw new RuntimeException("Error during test data persistence");
		}
	}

	/** After each test */
	@AfterEach
	void tearDown() throws TechnicalException {
		var session = hibernate.openSession();
		try {
			// Delete users
			session.createMutationQuery("DELETE FROM User").executeUpdate();
			session.flush();
			hibernate.commit(session);
		} catch (PersistenceException pex) {
			throw new RuntimeException("Error during test data cleanup");
		} finally {
			hibernate.rollback(session);
		}
	}

	/** Get user by id tests */
	@Nested
	@DisplayName("Get user by id tests")
	class GetByIdTests {

		@Test
		@DisplayName("Should return an existing user")
		void shouldReturnExistingUser() throws FunctionalException, TechnicalException {
			// When
			User user = userRepo.getById(testUser.getId());
			// Then
			assertNotNull(user);
			assertEquals(testUser.getId(), user.getId());
			assertEquals(testUser.getName(), user.getName());
			assertEquals(testUser.getEmail(), user.getEmail());
		}

		@Test
		@DisplayName("Should throw UnrecognizedUserException when user does not exist")
		void shouldThrowUnrecognizedUserExceptionWhenUserDoesNotExist() {
			// When & Then
			UnrecognizedUserException uuex = assertThrows(UnrecognizedUserException.class, () -> userRepo.getById(999));
			assertNotNull(uuex.getMessage());
		}
	}

	/** Get user by credentials tests */
	@Nested
	@DisplayName("Get user by credentials tests")
	class GetByCredentialsTests {

		@Test
		@DisplayName("Should return a user with email / password")
		void shouldReturnUserWithEmailPassword() throws FunctionalException, TechnicalException {
			// When
			User user = userRepo.getByCredentials(testUser.getEmail(), testUser.getSecret(), null);
			// Then
			assertNotNull(user);
			assertEquals(testUser.getId(), user.getId());
			assertEquals(testUser.getEmail(), user.getEmail());
			assertNotNull(user.getRefreshToken());
			assertNotNull(user.getRefreshTokenExpiry());
		}

		@Test
		@DisplayName("Should throw UnrecognizedUserException with wrong credentials")
		void shouldThrowUnrecognizedUserExceptionWithWrongCredentials() {
			// When & Then
			UnrecognizedUserException uuex = assertThrows(UnrecognizedUserException.class,
					() -> userRepo.getByCredentials("wrong@email.com", "wrong_password", null));
			assertNotNull(uuex.getMessage());
		}
	}

	/** Get user by email and password tests */
	@Nested
	@DisplayName("Get user by email and password tests")
	class GetByEmailAndPasswordTests {

		@Test
		@DisplayName("Should return a user with valid email / password")
		void shouldReturnUserWithValidEmailPassword() throws FunctionalException, TechnicalException {
			// When
			User user = userRepo.getByEmailAndPassword(testUser.getEmail(), testUser.getSecret());
			// Then
			assertNotNull(user);
			assertEquals(testUser.getId(), user.getId());
			assertEquals(testUser.getEmail(), user.getEmail());
			assertNotNull(user.getRefreshToken());
			assertNotNull(user.getRefreshTokenExpiry());
		}

		@Test
		@DisplayName("Should throw UnrecognizedUserException with invalid email / password")
		void shouldThrowUnrecognizedUserExceptionWithInvalidEmailPassword() {
			// When & Then
			UnrecognizedUserException uuex = assertThrows(UnrecognizedUserException.class,
					() -> userRepo.getByEmailAndPassword("wrong@email.com", "wrong_password"));
			assertNotNull(uuex.getMessage());
		}
	}

	/** Get user by refresh token tests */
	@Nested
	@DisplayName("Get user by refresh token tests")
	class GetByRefreshTokenTests {

		@Test
		@DisplayName("Should return a user with valid refresh token")
		void shouldReturnUserWithValidRefreshToken() throws FunctionalException, TechnicalException {
			// When
			User user = userRepo.getByRefreshToken(testUser.getRefreshToken());
			// Then
			assertNotNull(user);
			assertEquals(testUser.getId(), user.getId());
			assertNotNull(user.getRefreshToken());
			assertNotNull(user.getRefreshTokenExpiry());
		}

		@Test
		@DisplayName("Should throw UnrecognizedUserException with invalid refresh token")
		void shouldThrowUnrecognizedUserExceptionWithInvalidRefreshToken() {
			// When & Then
			UnrecognizedUserException uuex = assertThrows(UnrecognizedUserException.class,
					() -> userRepo.getByRefreshToken("invalid_refresh_token"));
			assertNotNull(uuex.getMessage());
		}
	}

	/** Add user tests */
	@Nested
	@DisplayName("Add user tests")
	class AddTests {

		@Test
		@DisplayName("Should add a new user")
		void shouldAddNewUser() throws FunctionalException, TechnicalException {
			// Given
			String name = "New User";
			String email = "new@example.com";
			String password = "new_password";
			// When
			User newUser = userRepo.add(name, email, password);
			// Then
			assertNotNull(newUser);
			assertEquals(name, newUser.getName());
			assertEquals(email, newUser.getEmail());
			assertEquals(password, newUser.getSecret());
			assertNotNull(newUser.getRefreshToken());
			assertNotNull(newUser.getRefreshTokenExpiry());
		}

		@Test
		@DisplayName("Should throw AlreadyExistException for existing email / password")
		void shouldThrowAlreadyExistExceptionForExistingEmailPassword() {
			// When & Then
			AlreadyExistUserException aeex = assertThrows(AlreadyExistUserException.class,
					() -> userRepo.add("Test User", testUser.getEmail(), testUser.getSecret()));
			assertNotNull(aeex.getMessage());
		}
	}

	/** Update user tests */
	@Nested
	@DisplayName("Update user tests")
	class UpdateTests {

		@Test
		@DisplayName("Should update an existing user")
		void shouldUpdateExistingUser() throws FunctionalException, TechnicalException {
			// When
			User updatedUser = userRepo.update(testUser.getRefreshToken());
			// Then
			assertNotNull(updatedUser);
			assertEquals(testUser.getId(), updatedUser.getId());
			assertNull(updatedUser.getRefreshToken());
			assertNull(updatedUser.getRefreshTokenExpiry());
		}

		@Test
		@DisplayName("Should throw NotExistUserException when refresh token is invalid")
		void shouldThrowNotExistUserExceptionWhenRefreshTokenIsInvalid() {
			// When & Then
			NotExistUserException neuex = assertThrows(NotExistUserException.class,
					() -> userRepo.update("invalid_refresh_token"));
			assertNotNull(neuex.getMessage());
		}
	}
}
