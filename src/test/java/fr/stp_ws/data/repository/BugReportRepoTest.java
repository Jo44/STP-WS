package fr.stp_ws.data.repository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;

import org.hibernate.Session;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.data.model.BugReport;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import jakarta.persistence.PersistenceException;

/**
 * Bug report repository tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Bug report repository tests")
class BugReportRepoTest {

	private static Hibernate hibernateMock;
	private BugReportRepo bugReportRepo;
	private IUserRepo userRepo;
	private User testUser;
	private Session sessionMock;

	/** Before all tests */
	@BeforeAll
	static void setUpClass() {
		// Use a Hibernate mock instead of initializing the real connection
		hibernateMock = mock(Hibernate.class);
	}

	/** After all tests */
	@AfterAll
	static void tearDownClass() {
		// Nothing to do with a mock
	}

	/** Before each test */
	@BeforeEach
	void setUp() throws TechnicalException, FunctionalException {
		// Create session mock
		sessionMock = mock(Session.class);
		// Configure Hibernate mock
		when(hibernateMock.openSession()).thenReturn(sessionMock);
		doNothing().when(hibernateMock).commit(sessionMock);
		doNothing().when(hibernateMock).rollback(sessionMock);
		// Create IUserRepo mock
		userRepo = mock(IUserRepo.class);
		// Bug report repository with mocked Hibernate and UserRepo
		bugReportRepo = new BugReportRepo(hibernateMock, userRepo);

		// Create test user
		testUser = new User();
		testUser.setId(1);
		testUser.setName("Test User");
		testUser.setEmail("test@example.com");
		testUser.setTourist(false);
		testUser.setGoogle(false);
		testUser.setSecret("test_secret");
		testUser.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testUser.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		testUser.setPlaces(new ArrayList<>());
		testUser.setPlacelists(new ArrayList<>());
		testUser.setComments(new ArrayList<>());
	}

	/** Collect bug report tests */
	@Nested
	@DisplayName("Collect bug report tests")
	class AddTests {

		@Test
		@DisplayName("Should add a bug report successfully")
		void shouldAddBugReportSuccessfully() throws TechnicalException, FunctionalException {
			// Given
			Integer validUserId = 1;
			String description = "Bug description";
			String fileName = "error.log";
			// Configure mock to return a valid user
			when(userRepo.getById(validUserId)).thenReturn(testUser);
			// When
			bugReportRepo.add(description, fileName, validUserId);
			// Then - Check session persisted the report
			verify(sessionMock).merge(any(BugReport.class));
		}

		@Test
		@DisplayName("Should throw a TechnicalException on persistence error")
		void shouldThrowTechnicalExceptionOnPersistenceError() throws TechnicalException, FunctionalException {
			// Given
			Integer validUserId = 1;
			String description = "Bug description";
			String fileName = "error.log";
			// Configure mock to return a valid user
			when(userRepo.getById(validUserId)).thenReturn(testUser);
			// Configure session mock to throw an exception during persist
			doThrow(new PersistenceException("Simulated error")).when(sessionMock).merge(any(BugReport.class));
			// When/Then
			TechnicalException tex = assertThrows(TechnicalException.class,
					() -> bugReportRepo.add(description, fileName, validUserId));
			assertNotNull(tex.getMessage());
		}

		@Test
		@DisplayName("Should throw a FunctionalException if user does not exist")
		void shouldThrowFunctionalExceptionIfUserDoesNotExist() throws TechnicalException, FunctionalException {
			// Given
			Integer invalidUserId = 9999;
			String description = "Bug description";
			String fileName = "error.log";
			// Configure mock to throw an exception if user does not exist
			when(userRepo.getById(invalidUserId)).thenThrow(new FunctionalException("User not found"));
			// When & Then
			FunctionalException exception = assertThrows(FunctionalException.class,
					() -> bugReportRepo.add(description, fileName, invalidUserId));
			assertNotNull(exception.getMessage());
		}
	}
}
