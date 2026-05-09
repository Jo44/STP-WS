package fr.stp_ws.presentation.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.Principal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.presentation.exception.NotAuthorizedException;

/**
 * Security context components tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Security context components tests")
class SecurityContextComponentsTest {

	@Test
	@DisplayName("Should expose user ID as principal name")
	void shouldExposeUserIdAsPrincipalName() {
		// Given
		UserPrincipal principal = new UserPrincipal(42);
		// When
		String principalName = principal.getName();
		// Then
		assertEquals("42", principalName);
	}

	@Test
	@DisplayName("Should expose security context values")
	void shouldExposeSecurityContextValues() {
		// Given
		Principal principal = new UserPrincipal(7);
		TokenSecurityContext securityContext = new TokenSecurityContext(principal, true, "Bearer");
		// Then
		assertEquals(principal, securityContext.getUserPrincipal());
		assertTrue(securityContext.isSecure());
		assertEquals("Bearer", securityContext.getAuthenticationScheme());
		assertFalse(securityContext.isUserInRole("ADMIN"));
	}

	@Test
	@DisplayName("Should extract user ID from security context")
	void shouldExtractUserIdFromSecurityContext() throws Exception {
		// Given
		TokenSecurityContext securityContext = new TokenSecurityContext(new UserPrincipal(13), false, "Bearer");
		// When
		Integer userId = SecurityContextUser.getUserID(securityContext);
		// Then
		assertEquals(13, userId);
	}

	@Test
	@DisplayName("Should reject missing principal in security context")
	void shouldRejectMissingPrincipalInSecurityContext() {
		// Given
		TokenSecurityContext securityContext = new TokenSecurityContext(null, false, "Bearer");
		// Then
		assertThrows(NotAuthorizedException.class, () -> SecurityContextUser.getUserID(securityContext));
	}

	@Test
	@DisplayName("Should reject non numeric principal ID")
	void shouldRejectNonNumericPrincipalId() {
		// Given
		Principal principal = new Principal() {
			@Override
			public String getName() {
				return "abc";
			}
		};
		TokenSecurityContext securityContext = new TokenSecurityContext(principal, false, "Bearer");
		// Then
		assertThrows(NotAuthorizedException.class, () -> SecurityContextUser.getUserID(securityContext));
	}
}
