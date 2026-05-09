package fr.stp_ws.presentation.security;

import java.security.Principal;

/**
 * User principal
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class UserPrincipal implements Principal {

	private final Integer userId;

	/**
	 * Constructor
	 *
	 * @param userId
	 */
	public UserPrincipal(Integer userId) {
		this.userId = userId;
	}

	/**
	 * Get name
	 *
	 * @return String
	 */
	@Override
	public String getName() {
		return String.valueOf(userId);
	}
}
