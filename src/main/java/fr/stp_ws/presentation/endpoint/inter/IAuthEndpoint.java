package fr.stp_ws.presentation.endpoint.inter;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.auth.LogInDTO;
import fr.stp_ws.domain.model.dto.auth.SignUpDTO;
import fr.stp_ws.domain.model.dto.auth.StateDTO;
import fr.stp_ws.domain.model.dto.auth.TokenDTO;
import fr.stp_ws.domain.model.dto.auth.UserDTO;
import fr.stp_ws.presentation.validator.RequestValidator;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Authentication endpoints interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IAuthEndpoint {

	/**
	 * Get API state
	 *
	 * @return StateDTO
	 */
	@GET
	@Path("/state")
	public StateDTO getState();

	/**
	 * User sign-up
	 *
	 * @param validator
	 * @param signUpDTO
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/signup")
	public UserDTO signup(@BeanParam RequestValidator validator, SignUpDTO signUpDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * User login
	 *
	 * @param validator
	 * @param logInDTO
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/login")
	public UserDTO login(@BeanParam RequestValidator validator, LogInDTO logInDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * Refresh STPT
	 *
	 * @param validator
	 * @param tokenDTO
	 * @return TokenDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/refresh-stpt")
	public TokenDTO refreshSTPT(@BeanParam RequestValidator validator, TokenDTO tokenDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * Refresh JWT
	 *
	 * @param validator
	 * @param tokenDTO
	 * @return TokenDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/refresh-jwt")
	public TokenDTO refreshJWT(@BeanParam RequestValidator validator, TokenDTO tokenDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * User logout
	 *
	 * @param validator
	 * @param tokenDTO
	 * @return UserDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/logout")
	public UserDTO logout(@BeanParam RequestValidator validator, TokenDTO tokenDTO)
			throws FunctionalException, TechnicalException;
}
