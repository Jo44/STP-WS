package fr.stp_ws.application.security;

import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;

/**
 * Token service interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface ITokenService {

	/**
	 * Generate STPT for user
	 *
	 * @param user
	 * @return String
	 * @throws FunctionalException
	 */
	public String generateSTPT(User user) throws FunctionalException;

	/**
	 * Generate JWT for user
	 *
	 * @param user
	 * @return String
	 * @throws FunctionalException
	 */
	public String generateJWT(User user) throws FunctionalException;

	/**
	 * Get user ID from STPT
	 *
	 * @param stpt
	 * @return Integer
	 * @throws FunctionalException
	 */
	public Integer getIDFromSTPT(String stpt) throws FunctionalException;

	/**
	 * Get user ID from JWT
	 *
	 * @param jwt
	 * @return Integer
	 * @throws FunctionalException
	 */
	public Integer getIDFromJWT(String jwt) throws FunctionalException;
}
