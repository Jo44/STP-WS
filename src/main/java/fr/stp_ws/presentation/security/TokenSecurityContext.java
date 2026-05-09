package fr.stp_ws.presentation.security;

import java.security.Principal;

import jakarta.ws.rs.core.SecurityContext;

/**
 * Token security context
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class TokenSecurityContext implements SecurityContext {

	private final Principal principal;
	private final boolean secure;
	private final String scheme;

	/**
	 * Constructor
	 *
	 * @param principal
	 * @param secure
	 * @param scheme
	 */
	public TokenSecurityContext(Principal principal, boolean secure, String scheme) {
		this.principal = principal;
		this.secure = secure;
		this.scheme = scheme;
	}

	/**
	 * Get user principal
	 *
	 * @return Principal
	 */
	@Override
	public Principal getUserPrincipal() {
		return principal;
	}

	/**
	 * Check if user is in role
	 *
	 * @param role
	 * @return boolean
	 */
	@Override
	public boolean isUserInRole(String role) {
		return false;
	}

	/**
	 * Check if secure
	 *
	 * @return boolean
	 */
	@Override
	public boolean isSecure() {
		return secure;
	}

	/**
	 * Get authentication scheme
	 *
	 * @return String
	 */
	@Override
	public String getAuthenticationScheme() {
		return scheme;
	}
}
