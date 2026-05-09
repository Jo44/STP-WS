package fr.stp_ws.application.usecase;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import fr.stp_ws.application.repository.IBugReportRepo;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.usecase.impl.BugReportUC;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.BugReportResponse;

/**
 * Bug report use-cases tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Bug report use-cases tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class BugReportUCTest {

	@InjectMocks
	private BugReportUC bugReportUC;
	@Mock
	private IUserRepo userRepo;
	@Mock
	private IBugReportRepo bugReportRepo;
	private User testUser;

	/** Before each test */
	@BeforeEach
	void setUp() throws FunctionalException, TechnicalException, IOException {
		// User initialization
		testUser = new User();
		testUser.setId(1);
		// userRepo mock configuration
		when(userRepo.getById(testUser.getId())).thenReturn(testUser);
	}

	/** Bug report collection tests */
	@Nested
	@DisplayName("Bug report collection tests")
	class CollectBugReportTests {

		@Test
		@DisplayName("Should collect bug report successfully")
		void shouldCollectBugReportSuccessfully() throws FunctionalException, TechnicalException, IOException {
			// Given
			Integer userId = 1;
			String description = "Bug description";
			String fileName = "log.txt";
			byte[] content = "Log file content".getBytes();
			InputStream logFile = new ByteArrayInputStream(content);
			doNothing().when(bugReportRepo).add(anyString(), anyString(), anyInt());
			// When
			BugReportResponse response = bugReportUC.collectBugReport(userId, description, logFile, fileName);
			// Then
			assertNotNull(response);
			assertTrue(response.isCollected());
			assertNotNull(response.getTimestamp());
			// Verify repository calls
			verify(bugReportRepo).add(description, fileName, userId);
		}

		@Test
		@DisplayName("Should handle IO error when saving file")
		void shouldHandleIOExceptionWhenSavingFile() throws FunctionalException, TechnicalException, IOException {
			// Given
			Integer userId = 1;
			String description = "Bug description";
			// Mock InputStream that throws IOException on read
			InputStream mockInputStream = mock(InputStream.class);
			when(mockInputStream.read(any(byte[].class))).thenThrow(new IOException("Simulated IO error"));
			// When/Then
			TechnicalException tex = assertThrows(TechnicalException.class,
					() -> bugReportUC.collectBugReport(userId, description, mockInputStream, "log.txt"));
			assertNotNull(tex.getMessage());
			verify(bugReportRepo, never()).add(anyString(), anyString(), anyInt());
		}

		@Test
		@DisplayName("Should handle user not found in repository")
		void shouldHandleUserNotFound() throws FunctionalException, TechnicalException {
			// Given
			Integer invalidUserId = 99;
			String description = "Bug description";
			InputStream logFile = new ByteArrayInputStream("Content".getBytes());
			when(userRepo.getById(invalidUserId)).thenReturn(null);
			// When
			BugReportResponse response = bugReportUC.collectBugReport(invalidUserId, description, logFile, "log.txt");
			// Then
			assertNotNull(response);
			assertTrue(response.isCollected());
			verify(bugReportRepo).add(description, "log.txt", invalidUserId);
		}
	}
}
