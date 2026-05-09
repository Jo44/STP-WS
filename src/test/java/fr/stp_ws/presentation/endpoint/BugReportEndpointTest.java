package fr.stp_ws.presentation.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.Principal;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.application.usecase.inter.IBugReportUC;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.BugReportResponse;
import fr.stp_ws.presentation.endpoint.impl.BugReportEndpoint;
import jakarta.ws.rs.core.SecurityContext;

/**
 * Bug report endpoint tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Bug report endpoint tests")
class BugReportEndpointTest {

	private static final Integer USER_ID = 1;
	private BugReportEndpoint bugReportEndpoint;
	private IBugReportUC bugReportUC;
	private SecurityContext securityContext;

	/** Before each test */
	@BeforeEach
	void setUp() throws Exception {
		// Wire mocks into endpoint
		bugReportUC = mock(IBugReportUC.class);
		securityContext = mock(SecurityContext.class);
		Principal principal = () -> String.valueOf(USER_ID);
		when(securityContext.getUserPrincipal()).thenReturn(principal);
		bugReportEndpoint = new BugReportEndpoint(bugReportUC);

		Field securityContextField = BugReportEndpoint.class.getDeclaredField("securityContext");
		securityContextField.setAccessible(true);
		securityContextField.set(bugReportEndpoint, securityContext);
	}

	/** Submit bug report tests */
	@Nested
	@DisplayName("Submit bug report tests")
	class SubmitBugReportTests {

		@Test
		@DisplayName("Should collect bug report successfully")
		void shouldCollectBugReportSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			String description = "Bug description";
			String fileName = "log.txt";
			InputStream logFile = new ByteArrayInputStream("Log file content".getBytes());
			FormDataContentDisposition fileDetail = mock(FormDataContentDisposition.class);
			when(fileDetail.getFileName()).thenReturn(fileName);
			BugReportResponse expectedResponse = new BugReportResponse(true);
			when(bugReportUC.collectBugReport(eq(USER_ID), eq(description), any(InputStream.class), anyString()))
					.thenReturn(expectedResponse);
			// When
			BugReportResponse response = bugReportEndpoint.collectBugReport(description, logFile, fileDetail);
			// Then
			assertNotNull(response);
			assertTrue(response.isCollected());
			assertNotNull(response.getTimestamp());
			verify(bugReportUC).collectBugReport(eq(USER_ID), eq(description), any(InputStream.class), anyString());
		}

		@Test
		@DisplayName("Should handle exception during collection")
		void shouldHandleExceptionDuringCollection() throws FunctionalException, TechnicalException {
			// Given
			String description = "Bug description";
			InputStream logFile = new ByteArrayInputStream("Log file content".getBytes());
			FormDataContentDisposition fileDetail = mock(FormDataContentDisposition.class);
			when(bugReportUC.collectBugReport(anyInt(), anyString(), any(InputStream.class), anyString()))
					.thenThrow(new TechnicalException("Simulated error"));
			// When
			BugReportResponse response = bugReportEndpoint.collectBugReport(description, logFile, fileDetail);
			// Then
			assertNotNull(response);
			assertEquals(false, response.isCollected());
		}

		@Test
		@DisplayName("Should handle unauthenticated user")
		void shouldHandleUnauthenticatedUser() throws FunctionalException, TechnicalException {
			// Given
			when(securityContext.getUserPrincipal()).thenReturn(null);
			String description = "Bug description";
			InputStream logFile = new ByteArrayInputStream("Log file content".getBytes());
			FormDataContentDisposition fileDetail = mock(FormDataContentDisposition.class);
			// When
			BugReportResponse response = bugReportEndpoint.collectBugReport(description, logFile, fileDetail);
			// Then
			assertNotNull(response);
			assertEquals(false, response.isCollected());
		}
	}
}
