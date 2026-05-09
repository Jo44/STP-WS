package fr.stp_ws.presentation.endpoint.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.usecase.inter.IAuthUC;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.auth.LogInDTO;
import fr.stp_ws.domain.model.dto.auth.SignUpDTO;
import fr.stp_ws.domain.model.dto.auth.StateDTO;
import fr.stp_ws.domain.model.dto.auth.TokenDTO;
import fr.stp_ws.domain.model.dto.auth.UserDTO;
import fr.stp_ws.presentation.endpoint.inter.IAuthEndpoint;
import fr.stp_ws.presentation.exception.InvalidRequestException;
import fr.stp_ws.presentation.validator.RequestValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;

/**
 * Authentication endpoints implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
@Path("/auth")
public class AuthEndpoint extends AbstractEndpoint implements IAuthEndpoint {

	private static final Logger LOGGER = LogManager.getLogger(AuthEndpoint.class);
	private final IAuthUC authUC;

	/** Constructor */
	@Inject
	public AuthEndpoint(IAuthUC authUC) {
		super();
		this.authUC = authUC;
	}

	/* Authentication - [GET] / [POST] */

	/**
	 * Get API state
	 *
	 * @return StateDTO
	 */
	// Endpoint: /auth/state
	@Override
	public StateDTO getState() {
		// Generate response
		return new StateDTO(true);
	}

	/**
	 * User sign-up
	 *
	 * @param validator
	 * @param signUpDTO
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /auth/signup
	@Override
	public UserDTO signup(RequestValidator validator, SignUpDTO signUpDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Authentication Endpoint --> [POST] Sign-up - /signup");
		return execute(LOGGER, () -> {
			// Validate request
			if (validator.checkRequestSignup(signUpDTO)) {
				// Create user from credentials
				return authUC.createUserFromCredentials(signUpDTO.getName(), signUpDTO.getEmail(),
						signUpDTO.getPassword());
			} else {
				throw new InvalidRequestException("Invalid sign-up information");
			}
		});
	}

	/**
	 * User login
	 *
	 * @param validator
	 * @param logInDTO
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /auth/login
	@Override
	public UserDTO login(RequestValidator validator, LogInDTO logInDTO) throws FunctionalException, TechnicalException {
		LOGGER.info("Authentication Endpoint --> [POST] Login - /login");
		return execute(LOGGER, () -> {
			// Validate request
			if (validator.checkRequestLogin(logInDTO)) {
				// Retrieve user from credentials
				return authUC.getUserFromCredentials(logInDTO.getEmail(), logInDTO.getPassword(),
						logInDTO.getGoogleToken());
			} else {
				throw new InvalidRequestException("Invalid login information");
			}
		});
	}

	/**
	 * Refresh STPT
	 *
	 * @param validator
	 * @param tokenDTO
	 * @return TokenDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /auth/refresh-stpt
	@Override
	public TokenDTO refreshSTPT(RequestValidator validator, TokenDTO tokenDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Authentication Endpoint --> [POST] Refresh STPT - /refresh-stpt");
		return execute(LOGGER, () -> {
			// Validate request
			if (validator.checkRequestToken(tokenDTO)) {
				// Retrieve user from refresh token
				UserDTO userDTO = authUC.getUserFromRefreshToken(tokenDTO.getToken());
				// Create token DTO
				return new TokenDTO(userDTO.getStpt());
			} else {
				throw new InvalidRequestException("Invalid STPT refresh information");
			}
		});
	}

	/**
	 * Refresh JWT
	 *
	 * @param validator
	 * @param tokenDTO
	 * @return TokenDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /auth/refresh-jwt
	@Override
	public TokenDTO refreshJWT(RequestValidator validator, TokenDTO tokenDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Authentication Endpoint --> [POST] Refresh JWT - /refresh-jwt");
		return execute(LOGGER, () -> {
			// Validate request
			if (validator.checkRequestToken(tokenDTO)) {
				// Generate JWT from STPT
				String jwt = authUC.generateJWTFromSTPT(tokenDTO.getToken());
				// Create token DTO
				return new TokenDTO(jwt);
			} else {
				throw new InvalidRequestException("Invalid JWT refresh information");
			}
		});
	}

	/**
	 * User logout
	 *
	 * @param validator
	 * @param tokenDTO
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /auth/logout
	@Override
	public UserDTO logout(RequestValidator validator, TokenDTO tokenDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Authentication Endpoint --> [POST] Logout - /logout");
		return execute(LOGGER, () -> {
			// Validate request
			if (validator.checkRequestToken(tokenDTO)) {
				// Logout user
				return authUC.logoutWithRefreshToken(tokenDTO.getToken());
			} else {
				throw new InvalidRequestException("Invalid logout information");
			}
		});
	}
}
