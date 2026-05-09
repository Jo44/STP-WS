package fr.stp_ws.presentation.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.application.usecase.inter.IAuthUC;
import fr.stp_ws.domain.exception.AlreadyExistUserException;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistUserException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.auth.LogInDTO;
import fr.stp_ws.domain.model.dto.auth.SignUpDTO;
import fr.stp_ws.domain.model.dto.auth.StateDTO;
import fr.stp_ws.domain.model.dto.auth.TokenDTO;
import fr.stp_ws.domain.model.dto.auth.UserDTO;
import fr.stp_ws.presentation.endpoint.impl.AuthEndpoint;
import fr.stp_ws.presentation.validator.RequestValidator;

/**
 * Authentication endpoint tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Authentication endpoint tests")
class AuthEndpointTest {

	private AuthEndpoint authEndpoint;
	private IAuthUC authUC;
	private RequestValidator validator;

	/** Before each test */
	@BeforeEach
	void setUp() throws Exception {
		// Wire mocks into endpoint
		authUC = mock(IAuthUC.class);
		validator = mock(RequestValidator.class);
		authEndpoint = new AuthEndpoint(authUC);
	}

	/** Web service state tests */
	@Nested
	@DisplayName("Web service state tests")
	class StateTests {

		@Test
		@DisplayName("Should return web service state")
		void shouldReturnWebServiceState() {
			// When
			StateDTO response = authEndpoint.getState();
			// Then
			assertNotNull(response);
			assertTrue(response.getOnline());
		}
	}

	/** Sign up tests */
	@Nested
	@DisplayName("Sign up tests")
	class SignUpTests {

		@Test
		@DisplayName("Should sign up new user successfully")
		void shouldSignUpNewUser() throws FunctionalException, TechnicalException {
			// Given
			SignUpDTO request = new SignUpDTO();
			request.setName("Test User");
			request.setEmail("test@example.com");
			request.setPassword("password123");
			when(validator.checkRequestSignup(request)).thenReturn(true);
			UserDTO userResponse = new UserDTO();
			userResponse.setName(request.getName());
			userResponse.setEmail(request.getEmail());
			userResponse.setRefreshToken("refresh_token");
			userResponse.setStpt("stpt_token");
			userResponse.setJwt("jwt_token");
			when(authUC.createUserFromCredentials(request.getName(), request.getEmail(), request.getPassword()))
					.thenReturn(userResponse);
			// When
			UserDTO response = authEndpoint.signup(validator, request);
			// Then
			assertNotNull(response);
			assertEquals(userResponse, response);
			verify(authUC).createUserFromCredentials(request.getName(), request.getEmail(), request.getPassword());
		}

		@Test
		@DisplayName("Should throw when email already exists")
		void shouldReturnErrorWhenEmailAlreadyExists() throws FunctionalException, TechnicalException {
			// Given
			SignUpDTO request = new SignUpDTO();
			request.setName("Test User");
			request.setEmail("existing@example.com");
			request.setPassword("password123");
			when(validator.checkRequestSignup(request)).thenReturn(true);
			when(authUC.createUserFromCredentials(request.getName(), request.getEmail(), request.getPassword()))
					.thenThrow(new AlreadyExistUserException("Email already in use"));
			// When & Then
			AlreadyExistUserException alreadyExistException = assertThrows(AlreadyExistUserException.class,
					() -> authEndpoint.signup(validator, request));
			assertNotNull(alreadyExistException.getMessage());
		}

		@Test
		@DisplayName("Should throw when signup request is invalid")
		void shouldReturnErrorWhenRequestIsInvalid() {
			// Given
			SignUpDTO request = new SignUpDTO();
			request.setName("");
			request.setEmail("invalid-email");
			request.setPassword("");
			when(validator.checkRequestSignup(request)).thenReturn(false);
			// When & Then
			FunctionalException functionnalException = assertThrows(FunctionalException.class,
					() -> authEndpoint.signup(validator, request));
			assertNotNull(functionnalException.getMessage());
		}
	}

	/** Log in tests */
	@Nested
	@DisplayName("Log in tests")
	class LogInTests {

		@Test
		@DisplayName("Should log in user with email and password")
		void shouldLogInUserWithEmailPassword() throws FunctionalException, TechnicalException {
			// Given
			LogInDTO request = new LogInDTO();
			request.setEmail("test@example.com");
			request.setPassword("password123");
			request.setGoogleToken(null);
			when(validator.checkRequestLogin(request)).thenReturn(true);
			UserDTO userResponse = new UserDTO();
			userResponse.setName("Test User");
			userResponse.setEmail(request.getEmail());
			userResponse.setRefreshToken("refresh_token");
			userResponse.setStpt("stpt_token");
			userResponse.setJwt("jwt_token");
			when(authUC.getUserFromCredentials(request.getEmail(), request.getPassword(), request.getGoogleToken()))
					.thenReturn(userResponse);
			// When
			UserDTO response = authEndpoint.login(validator, request);
			// Then
			assertNotNull(response);
			assertEquals(userResponse, response);
			verify(authUC).getUserFromCredentials(request.getEmail(), request.getPassword(), request.getGoogleToken());
		}

		@Test
		@DisplayName("Should log in user with Google")
		void shouldLogInUserWithGoogle() throws FunctionalException, TechnicalException {
			// Given
			LogInDTO request = new LogInDTO();
			request.setEmail(null);
			request.setPassword(null);
			request.setGoogleToken("google_token");
			when(validator.checkRequestLogin(request)).thenReturn(true);
			UserDTO userResponse = new UserDTO();
			userResponse.setName("Google User");
			userResponse.setEmail("google@example.com");
			userResponse.setRefreshToken("refresh_token");
			userResponse.setStpt("stpt_token");
			userResponse.setJwt("jwt_token");
			when(authUC.getUserFromCredentials(null, null, request.getGoogleToken())).thenReturn(userResponse);
			// When
			UserDTO response = authEndpoint.login(validator, request);
			// Then
			assertNotNull(response);
			assertEquals(userResponse, response);
			verify(authUC).getUserFromCredentials(null, null, request.getGoogleToken());
		}

		@Test
		@DisplayName("Should throw when credentials are invalid")
		void shouldReturnErrorWhenCredentialsAreInvalid() throws FunctionalException, TechnicalException {
			// Given
			LogInDTO request = new LogInDTO();
			request.setEmail("test@example.com");
			request.setPassword("wrong_password");
			request.setGoogleToken(null);
			when(validator.checkRequestLogin(request)).thenReturn(true);
			when(authUC.getUserFromCredentials(request.getEmail(), request.getPassword(), request.getGoogleToken()))
					.thenThrow(new NotExistUserException("User does not exist"));
			// When & Then
			NotExistUserException neuex = assertThrows(NotExistUserException.class,
					() -> authEndpoint.login(validator, request));
			assertNotNull(neuex.getMessage());
		}

		@Test
		@DisplayName("Should throw when login request is invalid")
		void shouldReturnErrorWhenRequestIsInvalid() {
			// Given
			LogInDTO request = new LogInDTO();
			request.setEmail("");
			request.setPassword("");
			request.setGoogleToken(null);
			when(validator.checkRequestLogin(request)).thenReturn(false);
			// When & Then
			FunctionalException functionnalException = assertThrows(FunctionalException.class,
					() -> authEndpoint.login(validator, request));
			assertNotNull(functionnalException.getMessage());
		}
	}

	/** Refresh STPT and JWT tests */
	@Nested
	@DisplayName("Refresh STPT and JWT tests")
	class RefreshTests {

		@Test
		@DisplayName("Should refresh STPT successfully")
		void shouldRefreshSTPTSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("valid_refresh_token");
			when(validator.checkRequestToken(request)).thenReturn(true);
			UserDTO userResponse = new UserDTO();
			userResponse.setStpt("new_stpt_token");
			when(authUC.getUserFromRefreshToken(request.getToken())).thenReturn(userResponse);
			// When
			TokenDTO response = authEndpoint.refreshSTPT(validator, request);
			// Then
			assertNotNull(response);
			assertEquals("new_stpt_token", response.getToken());
			verify(authUC).getUserFromRefreshToken(request.getToken());
		}

		@Test
		@DisplayName("Should refresh JWT successfully")
		void shouldRefreshJWTSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("valid_stpt");
			when(validator.checkRequestToken(request)).thenReturn(true);
			when(authUC.generateJWTFromSTPT(request.getToken())).thenReturn("new_jwt_token");
			// When
			TokenDTO response = authEndpoint.refreshJWT(validator, request);
			// Then
			assertNotNull(response);
			assertEquals("new_jwt_token", response.getToken());
			verify(authUC).generateJWTFromSTPT(request.getToken());
		}

		@Test
		@DisplayName("Should throw when token is invalid for STPT refresh")
		void shouldReturnErrorWhenTokenIsInvalidForSTPT() throws FunctionalException, TechnicalException {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("invalid_token");
			when(validator.checkRequestToken(request)).thenReturn(true);
			when(authUC.getUserFromRefreshToken(request.getToken()))
					.thenThrow(new NotExistUserException("User does not exist"));
			// When & Then
			NotExistUserException neuex = assertThrows(NotExistUserException.class,
					() -> authEndpoint.refreshSTPT(validator, request));
			assertNotNull(neuex.getMessage());
		}

		@Test
		@DisplayName("Should throw when token is invalid for JWT refresh")
		void shouldReturnErrorWhenTokenIsInvalidForJWT() throws FunctionalException, TechnicalException {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("invalid_token");
			when(validator.checkRequestToken(request)).thenReturn(true);
			when(authUC.generateJWTFromSTPT(request.getToken()))
					.thenThrow(new NotExistUserException("User does not exist"));
			// When & Then
			NotExistUserException neuex = assertThrows(NotExistUserException.class,
					() -> authEndpoint.refreshJWT(validator, request));
			assertNotNull(neuex.getMessage());
		}

		@Test
		@DisplayName("Should throw when token refresh request is invalid")
		void shouldReturnErrorWhenRequestIsInvalid() {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("");
			when(validator.checkRequestToken(request)).thenReturn(false);
			// When & Then
			FunctionalException functionnalException = assertThrows(FunctionalException.class,
					() -> authEndpoint.refreshSTPT(validator, request));
			assertNotNull(functionnalException.getMessage());
		}
	}

	/** Log out tests */
	@Nested
	@DisplayName("Log out tests")
	class LogoutTests {

		@Test
		@DisplayName("Should log out user successfully")
		void shouldLogoutUserSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("valid_refresh_token");
			when(validator.checkRequestToken(request)).thenReturn(true);
			UserDTO userResponse = new UserDTO();
			userResponse.setName("Test User");
			userResponse.setEmail("test@example.com");
			when(authUC.logoutWithRefreshToken(request.getToken())).thenReturn(userResponse);
			// When
			UserDTO response = authEndpoint.logout(validator, request);
			// Then
			assertNotNull(response);
			assertEquals(userResponse, response);
			verify(authUC).logoutWithRefreshToken(request.getToken());
		}

		@Test
		@DisplayName("Should throw when refresh token is invalid on logout")
		void shouldReturnErrorWhenRefreshTokenIsInvalid() throws FunctionalException, TechnicalException {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("invalid_refresh_token");
			when(validator.checkRequestToken(request)).thenReturn(true);
			when(authUC.logoutWithRefreshToken(request.getToken()))
					.thenThrow(new NotExistUserException("User does not exist"));
			// When & Then
			NotExistUserException neuex = assertThrows(NotExistUserException.class,
					() -> authEndpoint.logout(validator, request));
			assertNotNull(neuex.getMessage());
		}

		@Test
		@DisplayName("Should throw when logout request is invalid")
		void shouldReturnErrorWhenRequestIsInvalid() {
			// Given
			TokenDTO request = new TokenDTO();
			request.setToken("");
			when(validator.checkRequestToken(request)).thenReturn(false);
			// When & Then
			FunctionalException functionnalException = assertThrows(FunctionalException.class,
					() -> authEndpoint.logout(validator, request));
			assertNotNull(functionnalException.getMessage());
		}
	}
}
