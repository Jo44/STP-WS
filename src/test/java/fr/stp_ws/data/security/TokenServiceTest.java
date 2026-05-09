package fr.stp_ws.data.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Token service tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Token service tests")
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

	@Mock
	private Settings settings;
	private TokenService tokenService;
	private User testUser;
	private String testStptKey;
	private String testJwtKey;

	/** Before each test */
	@BeforeEach
	void setUp() {
		Settings testSettings = new Settings();
		testStptKey = testSettings.getString("auth.stpt.key");
		testJwtKey = testSettings.getString("auth.jwt.key");
		// Mock configuration
		when(settings.getString("auth.stpt.key")).thenReturn(testStptKey);
		when(settings.getString("auth.jwt.key")).thenReturn(testJwtKey);
		// Service initialization
		tokenService = new TokenService(settings);
		// Create a user
		testUser = new User();
		testUser.setId(1);
		testUser.setName("Test User");
		testUser.setEmail("test@example.com");
	}

	/** Generate STPT tests */
	@Nested
	@DisplayName("Generate STPT tests")
	class GenerateSTPTTests {

		@Test
		@DisplayName("Should generate a valid STPT for a valid user")
		void shouldGenerateValidSTPTForValidUser() throws FunctionalException {
			// When
			String stpt = tokenService.generateSTPT(testUser);
			// Then
			assertNotNull(stpt);
			// Verify token payload
			Jwt<?, Claims> parsedStpt = Jwts.parser()
					.verifyWith(Keys.hmacShaKeyFor(testStptKey.getBytes(StandardCharsets.UTF_8))).build()
					.parseSignedClaims(stpt);
			Claims claims = parsedStpt.getPayload();
			assertEquals(testUser.getId().toString(), claims.getSubject());
			assertEquals(testUser.getName(), claims.get("name"));
			assertEquals(testUser.getEmail(), claims.get("email"));
			assertNotNull(claims.getExpiration());
			assertNotNull(claims.getIssuedAt());
			// Verify validity duration (24h)
			long validityDuration = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
			assertEquals(86400000, validityDuration); // 24h in milliseconds
		}

		@Test
		@DisplayName("Should throw an exception when user is null")
		void shouldThrowExceptionWhenUserIsNull() {
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class, () -> tokenService.generateSTPT(null));
			assertNotNull(fex.getMessage());
		}
	}

	/** Generate JWT tests */
	@Nested
	@DisplayName("Generate JWT tests")
	class GenerateJWTTests {

		@Test
		@DisplayName("Should generate a valid JWT for a valid user")
		void shouldGenerateValidJWTForValidUser() throws FunctionalException {
			// When
			String jwt = tokenService.generateJWT(testUser);
			// Then
			assertNotNull(jwt);
			// Verify token payload
			Jwt<?, Claims> parsedJwt = Jwts.parser()
					.verifyWith(Keys.hmacShaKeyFor(testJwtKey.getBytes(StandardCharsets.UTF_8))).build()
					.parseSignedClaims(jwt);
			Claims claims = parsedJwt.getPayload();
			assertEquals(testUser.getId().toString(), claims.getSubject());
			assertEquals(testUser.getName(), claims.get("name"));
			assertEquals(testUser.getEmail(), claims.get("email"));
			assertNotNull(claims.getExpiration());
			assertNotNull(claims.getIssuedAt());
			// Verify validity duration (1h)
			long validityDuration = claims.getExpiration().getTime() - claims.getIssuedAt().getTime();
			assertEquals(3600000, validityDuration); // 1h in milliseconds
		}

		@Test
		@DisplayName("Should throw an exception when user is null")
		void shouldThrowExceptionWhenUserIsNull() {
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class, () -> tokenService.generateJWT(null));
			assertNotNull(fex.getMessage());
		}
	}

	/** Get user ID from STPT tests */
	@Nested
	@DisplayName("Get user ID from STPT tests")
	class GetIDFromSTPTTests {

		@Test
		@DisplayName("Should extract user id from a valid STPT")
		void shouldExtractUserIdFromValidSTPT() throws FunctionalException {
			// Given
			String stpt = tokenService.generateSTPT(testUser);
			// When
			Integer extractedId = tokenService.getIDFromSTPT(stpt);
			// Then
			assertEquals(testUser.getId(), extractedId);
		}

		@Test
		@DisplayName("Should throw an exception for an expired STPT")
		void shouldThrowExceptionForExpiredSTPT() {
			// Given
			String expiredStpt = Jwts.builder().subject(testUser.getId().toString())
					.issuedAt(Date.from(Instant.now().minusSeconds(172800))) // -48h
					.expiration(Date.from(Instant.now().minusSeconds(86400))) // -24h
					.signWith(Keys.hmacShaKeyFor(testStptKey.getBytes(StandardCharsets.UTF_8))).compact();
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> tokenService.getIDFromSTPT(expiredStpt));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw an exception for a malformed STPT")
		void shouldThrowExceptionForMalformedSTPT() {
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> tokenService.getIDFromSTPT("token.malformed.invalid"));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw an exception for an STPT with invalid signature")
		void shouldThrowExceptionForInvalidSignatureSTPT() throws FunctionalException {
			// Given
			String stpt = tokenService.generateSTPT(testUser);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> tokenService.getIDFromSTPT(stpt + "invalid"));
			assertNotNull(fex.getMessage());
		}
	}

	/** Get user ID from JWT tests */
	@Nested
	@DisplayName("Get user ID from JWT tests")
	class GetIDFromJWTTests {

		@Test
		@DisplayName("Should extract user id from a valid JWT")
		void shouldExtractUserIdFromValidJWT() throws FunctionalException {
			// Given
			String jwt = tokenService.generateJWT(testUser);
			// When
			Integer extractedId = tokenService.getIDFromJWT(jwt);
			// Then
			assertEquals(testUser.getId(), extractedId);
		}

		@Test
		@DisplayName("Should throw an exception for an expired JWT")
		void shouldThrowExceptionForExpiredJWT() {
			// Given
			String expiredJwt = Jwts.builder().subject(testUser.getId().toString())
					.issuedAt(Date.from(Instant.now().minusSeconds(7200))) // -2h
					.expiration(Date.from(Instant.now().minusSeconds(3600))) // -1h
					.signWith(Keys.hmacShaKeyFor(testJwtKey.getBytes(StandardCharsets.UTF_8))).compact();
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> tokenService.getIDFromJWT(expiredJwt));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw an exception for a malformed JWT")
		void shouldThrowExceptionForMalformedJWT() {
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> tokenService.getIDFromJWT("token.malformed.invalid"));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw an exception for a JWT with invalid signature")
		void shouldThrowExceptionForInvalidSignatureJWT() throws FunctionalException {
			// Given
			String jwt = tokenService.generateJWT(testUser);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> tokenService.getIDFromJWT(jwt + "invalid"));
			assertNotNull(fex.getMessage());
		}
	}
}
