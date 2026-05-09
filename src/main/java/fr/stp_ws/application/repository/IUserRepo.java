package fr.stp_ws.application.repository;

import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;

/**
 * User repository interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IUserRepo {

	/**
	 * Get a user by ID
	 *
	 * @param userId
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public User getById(Integer userId) throws FunctionalException, TechnicalException;

	/**
	 * Get a user by credentials
	 *
	 * @param email
	 * @param password
	 * @param googleToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public User getByCredentials(String email, String password, String googleToken)
			throws FunctionalException, TechnicalException;

	/**
	 * Get a user by email and password
	 *
	 * @param email
	 * @param password
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public User getByEmailAndPassword(String email, String password) throws FunctionalException, TechnicalException;

	/**
	 * Get a user by Google token
	 *
	 * @param googleToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public User getByGoogleToken(String googleToken) throws FunctionalException, TechnicalException;

	/**
	 * Get a user by refresh token
	 *
	 * @param refreshToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public User getByRefreshToken(String refreshToken) throws FunctionalException, TechnicalException;

	/**
	 * Add a user
	 *
	 * @param name
	 * @param email
	 * @param password
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public User add(String name, String email, String password) throws FunctionalException, TechnicalException;

	/**
	 * Update a user
	 *
	 * @param refreshToken
	 * @return User
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public User update(String refreshToken) throws FunctionalException, TechnicalException;
}
