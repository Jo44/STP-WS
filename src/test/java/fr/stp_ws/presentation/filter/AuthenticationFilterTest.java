package fr.stp_ws.presentation.filter;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.stp_ws.application.security.ITokenService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

/**
 * Authentication filter tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Authentication filter tests")
@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

	private static final String VALID_TOKEN = "valid.jwt.token";
	@InjectMocks
	private AuthenticationFilter filter;
	@Mock
	private ITokenService tokenService;
	@Mock
	private ContainerRequestContext requestContext;
	@Mock
	private UriInfo uriInfo;

	/** Authentication filter behavior tests */
	@Nested
	@DisplayName("Authentication filter behavior tests")
	class FilterTests {

		/** Before each test */
		@BeforeEach
		void setUp() {
			// Default UriInfo on request context
			when(requestContext.getUriInfo()).thenReturn(uriInfo);
		}

		@Test
		@DisplayName("Should bypass auth paths without validating JWT")
		void shouldBypassAuthenticationRequests() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/auth/login");
			// When
			filter.filter(requestContext);
			// Then
			verify(requestContext, never()).abortWith(org.mockito.ArgumentMatchers.any());
			verify(tokenService, never()).getIDFromJWT(anyString());
		}

		@Test
		@DisplayName("Should bypass only auth routes and reject non-auth without bearer")
		void shouldBypassOnlyAuthRoutesAndRejectOthersWithoutBearer() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/auth/state", "api/place/list/ALL/false/eat/45.0/46.0/4.0/5.0");
			when(requestContext.getHeaderString("Authorization")).thenReturn(null);
			// When
			filter.filter(requestContext);
			filter.filter(requestContext);
			// Then
			verify(requestContext).abortWith(org.mockito.ArgumentMatchers
					.argThat(response -> response.getStatus() == ErrorCode.Unauthorized.get()
							&& MediaType.APPLICATION_JSON.equals(response.getMediaType().toString())
							&& ((ErrorResponse) response.getEntity()).getCode().equals("INVALID_REQUEST")));
			verify(tokenService, never()).getIDFromJWT(anyString());
		}

		@Test
		@DisplayName("Should not bypass when auth segment appears in non-auth path")
		void shouldNotBypassWhenAuthSegmentAppearsInNonAuthPath() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/place/auth/login");
			when(requestContext.getHeaderString("Authorization")).thenReturn(null);
			// When
			filter.filter(requestContext);
			// Then
			verify(requestContext).abortWith(org.mockito.ArgumentMatchers
					.argThat(response -> response.getStatus() == ErrorCode.Unauthorized.get()
							&& ((ErrorResponse) response.getEntity()).getCode().equals("INVALID_REQUEST")));
			verify(tokenService, never()).getIDFromJWT(anyString());
		}

		@Test
		@DisplayName("Should reject requests without Authorization header")
		void shouldRejectRequestsWithoutAuthorizationHeader() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/places");
			when(requestContext.getHeaderString("Authorization")).thenReturn(null);
			// When
			filter.filter(requestContext);
			// Then
			verify(requestContext).abortWith(org.mockito.ArgumentMatchers
					.argThat(response -> response.getStatus() == ErrorCode.Unauthorized.get()
							&& ((ErrorResponse) response.getEntity()).getCode().equals("INVALID_REQUEST")));
		}

		@Test
		@DisplayName("Should reject requests with malformed Authorization header")
		void shouldRejectRequestsWithMalformedAuthorizationHeader() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/places");
			when(requestContext.getHeaderString("Authorization")).thenReturn("malformed_header");
			// When
			filter.filter(requestContext);
			// Then
			verify(requestContext).abortWith(org.mockito.ArgumentMatchers
					.argThat(response -> response.getStatus() == ErrorCode.Unauthorized.get()
							&& ((ErrorResponse) response.getEntity()).getCode().equals("INVALID_REQUEST")));
		}

		@Test
		@DisplayName("Should reject requests with invalid JWT")
		void shouldRejectRequestsWithInvalidJWT() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/places");
			when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer invalid.jwt");
			when(tokenService.getIDFromJWT("invalid.jwt")).thenThrow(new FunctionalException("Invalid token"));
			// When
			filter.filter(requestContext);
			// Then
			verify(requestContext).abortWith(org.mockito.ArgumentMatchers
					.argThat(response -> response.getStatus() == ErrorCode.Unauthorized.get()
							&& ((ErrorResponse) response.getEntity()).getCode().equals("NOT_AUTHORIZED")));
		}

		@Test
		@DisplayName("Should accept requests with valid JWT and set security context")
		void shouldAcceptRequestsWithValidJWT() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/places");
			when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
			when(tokenService.getIDFromJWT(VALID_TOKEN)).thenReturn(1);
			// When
			filter.filter(requestContext);
			// Then
			verify(requestContext).setSecurityContext(org.mockito.ArgumentMatchers.any(SecurityContext.class));
			verify(requestContext, never()).abortWith(org.mockito.ArgumentMatchers.any());
		}

		@Test
		@DisplayName("Should propagate authenticated user ID in security context")
		void shouldPropagateAuthenticatedUserIdInSecurityContext() throws Exception {
			// Given
			when(uriInfo.getPath()).thenReturn("api/places");
			when(requestContext.getHeaderString("Authorization")).thenReturn("Bearer " + VALID_TOKEN);
			when(tokenService.getIDFromJWT(VALID_TOKEN)).thenReturn(1);
			// When
			filter.filter(requestContext);
			// Then
			ArgumentCaptor<SecurityContext> securityContextCaptor = ArgumentCaptor.forClass(SecurityContext.class);
			verify(requestContext).setSecurityContext(securityContextCaptor.capture());
			SecurityContext capturedSecurityContext = securityContextCaptor.getValue();
			Assertions.assertNotNull(capturedSecurityContext);
			Assertions.assertNotNull(capturedSecurityContext.getUserPrincipal());
			Assertions.assertEquals("1", capturedSecurityContext.getUserPrincipal().getName());
		}
	}
}
