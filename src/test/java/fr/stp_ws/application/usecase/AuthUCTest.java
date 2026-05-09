package fr.stp_ws.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.stp_ws.application.model.mapper.inter.IUserMapper;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.security.ITokenService;
import fr.stp_ws.application.usecase.impl.AuthUC;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.auth.UserDTO;

/**
 * Authentication use-cases tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Authentication use-cases tests")
@ExtendWith(MockitoExtension.class)
class AuthUCTest {

	private static final Integer USER_ID = 1;
	private static final String USER_NAME = "Test User";
	private static final String USER_EMAIL = "test@example.com";
	private static final String USER_PASSWORD = "password123";
	private static final String REFRESH_TOKEN = "refresh.token.123";
	private static final String STPT = "stpt.token.123";
	private static final String JWT = "jwt.token.123";
	private static final String GOOGLE_TOKEN = "google.token.123";
	private static final String INVALID_TOKEN = "invalid.token";
	@InjectMocks
	private AuthUC authUC;
	@Mock
	private IUserRepo userRepo;
	@Mock
	private IUserMapper userMapper;
	@Mock
	private ITokenService tokenService;
	private User testUser;
	private UserDTO testUserDTO;
	private UserDTO testUserDTOWithoutTokens;
	private UserDTO testUserDTOWithSTPT;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		testUser = new User();
		testUser.setId(USER_ID);
		testUser.setName(USER_NAME);
		testUser.setEmail(USER_EMAIL);
		testUser.setRefreshToken(REFRESH_TOKEN);
		// DTO variants initialization
		testUserDTO = new UserDTO(USER_NAME, USER_EMAIL, false, REFRESH_TOKEN, STPT, JWT);
		testUserDTOWithoutTokens = new UserDTO(USER_NAME, USER_EMAIL, false, null, null, null);
		testUserDTOWithSTPT = new UserDTO(USER_NAME, USER_EMAIL, false, REFRESH_TOKEN, STPT, null);
	}

	/** Get user by id tests */
	@Nested
	@DisplayName("Get user by id tests")
	class GetUserFromIDTests {

		@Test
		@DisplayName("Should return a user by id")
		void shouldReturnUserById() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(USER_ID)).thenReturn(testUser);
			when(userMapper.toDTO(testUser, null, null, null)).thenReturn(testUserDTOWithoutTokens);
			// When
			UserDTO result = authUC.getUserFromID(USER_ID);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			assertEquals(USER_EMAIL, result.getEmail());
			verify(userRepo).getById(USER_ID);
			verify(userMapper).toDTO(testUser, null, null, null);
		}

		@Test
		@DisplayName("Should throw an exception if id is invalid")
		void shouldThrowExceptionForInvalidId() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(-1)).thenThrow(new FunctionalException("Invalid id"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class, () -> authUC.getUserFromID(-1));
			assertNotNull(fex.getMessage());
		}
	}

	/** Get user from credentials tests */
	@Nested
	@DisplayName("Get user from credentials tests")
	class GetUserFromCredentialsTests {

		@Test
		@DisplayName("Should return user with tokens via email and password")
		void shouldReturnUserWithTokensViaEmailPassword() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getByCredentials(USER_EMAIL, USER_PASSWORD, null)).thenReturn(testUser);
			when(tokenService.generateSTPT(testUser)).thenReturn(STPT);
			when(tokenService.generateJWT(testUser)).thenReturn(JWT);
			when(userMapper.toDTO(testUser, REFRESH_TOKEN, STPT, JWT)).thenReturn(testUserDTO);
			// When
			UserDTO result = authUC.getUserFromCredentials(USER_EMAIL, USER_PASSWORD, null);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			assertEquals(USER_EMAIL, result.getEmail());
			assertEquals(REFRESH_TOKEN, result.getRefreshToken());
			assertEquals(STPT, result.getStpt());
			assertEquals(JWT, result.getJwt());
			verify(userRepo).getByCredentials(USER_EMAIL, USER_PASSWORD, null);
			verify(tokenService).generateSTPT(testUser);
			verify(tokenService).generateJWT(testUser);
		}

		@Test
		@DisplayName("Should return user with tokens via Google")
		void shouldReturnUserWithTokensViaGoogle() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getByCredentials(null, null, GOOGLE_TOKEN)).thenReturn(testUser);
			when(tokenService.generateSTPT(testUser)).thenReturn(STPT);
			when(tokenService.generateJWT(testUser)).thenReturn(JWT);
			when(userMapper.toDTO(testUser, REFRESH_TOKEN, STPT, JWT)).thenReturn(testUserDTO);
			// When
			UserDTO result = authUC.getUserFromCredentials(null, null, GOOGLE_TOKEN);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			assertEquals(USER_EMAIL, result.getEmail());
			assertEquals(REFRESH_TOKEN, result.getRefreshToken());
			assertEquals(STPT, result.getStpt());
			assertEquals(JWT, result.getJwt());
			verify(userRepo).getByCredentials(null, null, GOOGLE_TOKEN);
			verify(tokenService).generateSTPT(testUser);
			verify(tokenService).generateJWT(testUser);
		}

		@Test
		@DisplayName("Should throw an exception if credentials are invalid")
		void shouldReturnExceptionIfCredentialsAreInvalid() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getByCredentials(USER_EMAIL, USER_PASSWORD, null))
					.thenThrow(new FunctionalException("Invalid credentials"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> authUC.getUserFromCredentials(USER_EMAIL, USER_PASSWORD, null));
			assertNotNull(fex.getMessage());
		}
	}

	/** Get user from refresh token tests */
	@Nested
	@DisplayName("Get user from refresh token tests")
	class GetUserFromRefreshTokenTests {

		@Test
		@DisplayName("Should return user with a new STPT")
		void shouldReturnUserWithNewSTPT() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getByRefreshToken(REFRESH_TOKEN)).thenReturn(testUser);
			when(tokenService.generateSTPT(testUser)).thenReturn(STPT);
			when(userMapper.toDTO(testUser, REFRESH_TOKEN, STPT, null)).thenReturn(testUserDTOWithSTPT);
			// When
			UserDTO result = authUC.getUserFromRefreshToken(REFRESH_TOKEN);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			assertEquals(USER_EMAIL, result.getEmail());
			assertEquals(REFRESH_TOKEN, result.getRefreshToken());
			assertEquals(STPT, result.getStpt());
			verify(userRepo).getByRefreshToken(REFRESH_TOKEN);
			verify(tokenService).generateSTPT(testUser);
		}

		@Test
		@DisplayName("Should throw an exception if refresh token is invalid")
		void shouldReturnExceptionIfRefreshTokenIsInvalid() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getByRefreshToken(INVALID_TOKEN)).thenThrow(new FunctionalException("Invalid refresh token"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> authUC.getUserFromRefreshToken(INVALID_TOKEN));
			assertNotNull(fex.getMessage());
		}
	}

	/** Create user from credentials tests */
	@Nested
	@DisplayName("Create user from credentials tests")
	class CreateUserFromCredentialsTests {

		@Test
		@DisplayName("Should create a new user with tokens")
		void shouldCreateNewUserWithTokens() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.add(USER_NAME, USER_EMAIL, USER_PASSWORD)).thenReturn(testUser);
			when(tokenService.generateSTPT(testUser)).thenReturn(STPT);
			when(tokenService.generateJWT(testUser)).thenReturn(JWT);
			when(userMapper.toDTO(testUser, REFRESH_TOKEN, STPT, JWT)).thenReturn(testUserDTO);
			// When
			UserDTO result = authUC.createUserFromCredentials(USER_NAME, USER_EMAIL, USER_PASSWORD);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			assertEquals(USER_EMAIL, result.getEmail());
			assertEquals(REFRESH_TOKEN, result.getRefreshToken());
			assertEquals(STPT, result.getStpt());
			assertEquals(JWT, result.getJwt());
			verify(userRepo).add(USER_NAME, USER_EMAIL, USER_PASSWORD);
			verify(tokenService).generateSTPT(testUser);
			verify(tokenService).generateJWT(testUser);
		}

		@Test
		@DisplayName("Should throw an exception if user already exists")
		void shouldReturnExceptionIfUserAlreadyExists() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.add(USER_NAME, USER_EMAIL, USER_PASSWORD))
					.thenThrow(new FunctionalException("User already exists"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> authUC.createUserFromCredentials(USER_NAME, USER_EMAIL, USER_PASSWORD));
			assertNotNull(fex.getMessage());
		}
	}

	/** Log out with refresh token tests */
	@Nested
	@DisplayName("Log out with refresh token tests")
	class LogoutWithRefreshTokenTests {

		@Test
		@DisplayName("Should log out user")
		void shouldLogoutUser() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.update(REFRESH_TOKEN)).thenReturn(testUser);
			when(userMapper.toDTO(testUser, null, null, null)).thenReturn(testUserDTOWithoutTokens);
			// When
			UserDTO result = authUC.logoutWithRefreshToken(REFRESH_TOKEN);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			verify(userRepo).update(REFRESH_TOKEN);
			verify(userMapper).toDTO(testUser, null, null, null);
		}

		@Test
		@DisplayName("Should throw an exception if refresh token is invalid on logout")
		void shouldReturnExceptionIfRefreshTokenIsInvalid() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.update(INVALID_TOKEN)).thenThrow(new FunctionalException("Invalid refresh token"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> authUC.logoutWithRefreshToken(INVALID_TOKEN));
			assertNotNull(fex.getMessage());
		}
	}

	/** Generate STPT from refresh token tests */
	@Nested
	@DisplayName("Generate STPT from refresh token tests")
	class GenerateSTPTFromRefreshTokenTests {

		@Test
		@DisplayName("Should generate a new STPT")
		void shouldGenerateNewSTPT() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getByRefreshToken(REFRESH_TOKEN)).thenReturn(testUser);
			when(tokenService.generateSTPT(testUser)).thenReturn(STPT);
			// When
			String result = authUC.generateSTPTFromRefreshToken(REFRESH_TOKEN);
			// Then
			assertEquals(STPT, result);
			verify(userRepo).getByRefreshToken(REFRESH_TOKEN);
			verify(tokenService).generateSTPT(testUser);
		}
	}

	/** Generate JWT from STPT tests */
	@Nested
	@DisplayName("Generate JWT from STPT tests")
	class GenerateJWTFromSTPTTests {

		@Test
		@DisplayName("Should generate a new JWT")
		void shouldGenerateNewJWT() throws FunctionalException, TechnicalException {
			// Given
			when(tokenService.getIDFromSTPT(STPT)).thenReturn(USER_ID);
			when(userRepo.getById(USER_ID)).thenReturn(testUser);
			when(tokenService.generateJWT(testUser)).thenReturn(JWT);
			// When
			String result = authUC.generateJWTFromSTPT(STPT);
			// Then
			assertEquals(JWT, result);
			verify(tokenService).getIDFromSTPT(STPT);
			verify(userRepo).getById(USER_ID);
			verify(tokenService).generateJWT(testUser);
		}

		@Test
		@DisplayName("Should throw an exception if STPT is invalid")
		void shouldReturnExceptionIfSTPTIsInvalid() throws FunctionalException, TechnicalException {
			// Given
			when(tokenService.getIDFromSTPT("invalid_stpt")).thenThrow(new FunctionalException("Invalid STPT"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> authUC.generateJWTFromSTPT("invalid_stpt"));
			assertNotNull(fex.getMessage());
		}
	}
}
