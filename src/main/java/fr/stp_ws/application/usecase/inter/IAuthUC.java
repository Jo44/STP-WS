package fr.stp_ws.application.usecase.inter;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.auth.UserDTO;

/**
 * Authentication use-cases interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IAuthUC {

	/**
	 * Get user from ID
	 *
	 * @param userId
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public UserDTO getUserFromID(Integer userId) throws FunctionalException, TechnicalException;

	/**
	 * Get user from credentials
	 *
	 * @param email
	 * @param password
	 * @param googleToken
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public UserDTO getUserFromCredentials(String email, String password, String googleToken)
			throws FunctionalException, TechnicalException;

	/**
	 * Get user from refresh token
	 *
	 * @param refreshToken
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public UserDTO getUserFromRefreshToken(String refreshToken) throws FunctionalException, TechnicalException;

	/**
	 * Create user from credentials
	 *
	 * @param name
	 * @param email
	 * @param password
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public UserDTO createUserFromCredentials(String name, String email, String password)
			throws FunctionalException, TechnicalException;

	/**
	 * Logout user with refresh token
	 *
	 * @param refreshToken
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public UserDTO logoutWithRefreshToken(String refreshToken) throws FunctionalException, TechnicalException;

	/**
	 * Generate STPT from refresh token
	 *
	 * @param refreshToken
	 * @return String
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public String generateSTPTFromRefreshToken(String refreshToken) throws FunctionalException, TechnicalException;

	/**
	 * Generate JWT from STPT
	 *
	 * @param stpt
	 * @return String
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public String generateJWTFromSTPT(String stpt) throws FunctionalException, TechnicalException;
}
