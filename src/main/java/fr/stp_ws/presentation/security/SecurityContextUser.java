package fr.stp_ws.presentation.security;

import fr.stp_ws.presentation.exception.NotAuthorizedException;
import jakarta.ws.rs.core.SecurityContext;

/**
 * Security context user
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public final class SecurityContextUser {

	/** Constructor */
	private SecurityContextUser() {
		super();
	}

	/**
	 * Get user ID from security context
	 *
	 * @param securityContext
	 * @return Integer
	 * @throws NotAuthorizedException
	 */
	public static Integer getUserID(SecurityContext securityContext) throws NotAuthorizedException {
		Integer userId = null;
		if (securityContext == null || securityContext.getUserPrincipal() == null) {
			throw new NotAuthorizedException("Unauthorized request");
		}
		try {
			userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
		} catch (NumberFormatException ex) {
			throw new NotAuthorizedException("Unauthorized request");
		}
		return userId;
	}
}
