package fr.stp_ws.application.usecase.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.IUserMapper;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.security.ITokenService;
import fr.stp_ws.application.usecase.inter.IAuthUC;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.auth.UserDTO;
import jakarta.inject.Inject;

/**
 * Authentication use-cases implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class AuthUC implements IAuthUC {

	private static final Logger LOGGER = LogManager.getLogger(AuthUC.class);
	private final IUserRepo userRepo;
	private final ITokenService tokenService;
	private final IUserMapper userMapper;

	/** Constructor */
	@Inject
	public AuthUC(IUserRepo userRepo, ITokenService tokenService, IUserMapper userMapper) {
		this.userRepo = userRepo;
		this.tokenService = tokenService;
		this.userMapper = userMapper;
	}

	/**
	 * Get user from ID
	 *
	 * @param userId
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public UserDTO getUserFromID(Integer userId) throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving user from ID");
		// Retrieve user
		User user = userRepo.getById(userId);
		// Convert user to DTO
		return userMapper.toDTO(user, null, null, null);
	}

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
	@Override
	public UserDTO getUserFromCredentials(String email, String password, String googleToken)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving user from credentials");
		// Retrieve user
		User user = userRepo.getByCredentials(email, password, googleToken);
		// Generate tokens (refresh / STPT / JWT)
		String refreshToken = user.getRefreshToken();
		String stpt = tokenService.generateSTPT(user);
		String jwt = tokenService.generateJWT(user);
		// Convert user to DTO
		return userMapper.toDTO(user, refreshToken, stpt, jwt);
	}

	/**
	 * Get user from refresh token
	 *
	 * @param refreshToken
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public UserDTO getUserFromRefreshToken(String refreshToken) throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving user from refresh token");
		// Retrieve user
		User user = userRepo.getByRefreshToken(refreshToken);
		// Generate token (STPT)
		String stpt = tokenService.generateSTPT(user);
		// Convert user to DTO
		return userMapper.toDTO(user, refreshToken, stpt, null);
	}

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
	@Override
	public UserDTO createUserFromCredentials(String name, String email, String password)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Creating user from credentials");
		// Add user
		User user = userRepo.add(name, email, password);
		// Generate tokens (refresh / STPT / JWT)
		String refreshToken = user.getRefreshToken();
		String stpt = tokenService.generateSTPT(user);
		String jwt = tokenService.generateJWT(user);
		// Convert user to DTO
		return userMapper.toDTO(user, refreshToken, stpt, jwt);
	}

	/**
	 * Logout user with refresh token
	 *
	 * @param refreshToken
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public UserDTO logoutWithRefreshToken(String refreshToken) throws FunctionalException, TechnicalException {
		LOGGER.debug("Logout user with refresh token");
		// Update user
		User user = userRepo.update(refreshToken);
		// Convert user to DTO
		return userMapper.toDTO(user, null, null, null);
	}

	/**
	 * Generate STPT from refresh token
	 *
	 * @param refreshToken
	 * @return String
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public String generateSTPTFromRefreshToken(String refreshToken) throws FunctionalException, TechnicalException {
		LOGGER.debug("Generating STPT from refresh token");
		// Retrieve user
		User user = userRepo.getByRefreshToken(refreshToken);
		// Generate STPT
		return tokenService.generateSTPT(user);
	}

	/**
	 * Generate JWT from STPT
	 *
	 * @param stpt
	 * @return String
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public String generateJWTFromSTPT(String stpt) throws FunctionalException, TechnicalException {
		LOGGER.debug("Generating JWT from STPT");
		// Retrieve user
		Integer userId = tokenService.getIDFromSTPT(stpt);
		User user = userRepo.getById(userId);
		// Generate JWT
		return tokenService.generateJWT(user);
	}
}
