package fr.stp_ws.application.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.stp_ws.application.model.mapper.impl.UserMapper;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.auth.UserDTO;

/**
 * User mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("User mapper tests")
@ExtendWith(MockitoExtension.class)
class UserMapperTest {

	private static final String USER_NAME = "Test User";
	private static final String USER_EMAIL = "test@example.com";
	private static final String REFRESH_TOKEN = "refresh.token.123";
	private static final String STPT = "stpt.token.123";
	private static final String JWT = "jwt.token.123";
	@InjectMocks
	private UserMapper userMapper;
	private User testUser;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		testUser = new User();
		testUser.setName(USER_NAME);
		testUser.setEmail(USER_EMAIL);
	}

	/** Map user to DTO tests */
	@Nested
	@DisplayName("Map user to DTO tests")
	class ToDTOTests {

		@Test
		@DisplayName("Should convert a valid user with all tokens")
		void shouldConvertValidUserWithAllTokens() {
			// When
			UserDTO result = userMapper.toDTO(testUser, REFRESH_TOKEN, STPT, JWT);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			assertEquals(USER_EMAIL, result.getEmail());
			assertEquals(REFRESH_TOKEN, result.getRefreshToken());
			assertEquals(STPT, result.getStpt());
			assertEquals(JWT, result.getJwt());
		}

		@Test
		@DisplayName("Should convert a valid user without tokens")
		void shouldConvertValidUserWithoutTokens() {
			// When
			UserDTO result = userMapper.toDTO(testUser, null, null, null);
			// Then
			assertNotNull(result);
			assertEquals(USER_NAME, result.getName());
			assertEquals(USER_EMAIL, result.getEmail());
			assertNull(result.getRefreshToken());
			assertNull(result.getStpt());
			assertNull(result.getJwt());
		}

		@Test
		@DisplayName("Should return null for a null user")
		void shouldReturnNullForNullUser() {
			// When
			UserDTO result = userMapper.toDTO(null, REFRESH_TOKEN, STPT, JWT);
			// Then
			assertNull(result);
		}
	}
}
