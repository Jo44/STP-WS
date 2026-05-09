package fr.stp_ws.presentation.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.model.dto.auth.LogInDTO;
import fr.stp_ws.domain.model.dto.auth.SignUpDTO;
import fr.stp_ws.domain.model.dto.auth.TokenDTO;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityType;

/**
 * Request validator tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Request validator tests")
class RequestValidatorTest {

	private RequestValidator validator;

	/** Before each test */
	@BeforeEach
	void setUp() {
		validator = new RequestValidator();
	}

	/** Sign up request validation tests */
	@Nested
	@DisplayName("Sign up request validation tests")
	class SignUpValidationTests {

		@Test
		@DisplayName("Should accept valid sign-up request")
		void shouldValidateValidSignUpRequest() {
			// Given
			SignUpDTO signUp = new SignUpDTO();
			signUp.setName("John Doe");
			signUp.setEmail("john.doe@example.com");
			signUp.setPassword("password123");
			// When
			Boolean result = validator.checkRequestSignup(signUp);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null sign-up request")
		void shouldRejectNullSignUpRequest() {
			// When
			Boolean result = validator.checkRequestSignup(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject sign-up request with invalid name")
		void shouldRejectSignUpRequestWithInvalidName() {
			// Given
			SignUpDTO signUp = new SignUpDTO();
			signUp.setName("Jo"); // Too short
			signUp.setEmail("john.doe@example.com");
			signUp.setPassword("password123");
			// When
			Boolean result = validator.checkRequestSignup(signUp);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject sign-up request with invalid email")
		void shouldRejectSignUpRequestWithInvalidEmail() {
			// Given
			SignUpDTO signUp = new SignUpDTO();
			signUp.setName("John Doe");
			signUp.setEmail("invalid.email"); // Invalid format
			signUp.setPassword("password123");
			// When
			Boolean result = validator.checkRequestSignup(signUp);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject sign-up request with invalid password")
		void shouldRejectSignUpRequestWithInvalidPassword() {
			// Given
			SignUpDTO signUp = new SignUpDTO();
			signUp.setName("John Doe");
			signUp.setEmail("john.doe@example.com");
			signUp.setPassword(""); // Empty
			// When
			Boolean result = validator.checkRequestSignup(signUp);
			// Then
			assertFalse(result);
		}
	}

	/** Log in request validation tests */
	@Nested
	@DisplayName("Log in request validation tests")
	class LoginValidationTests {

		@Test
		@DisplayName("Should accept valid login request with email and password")
		void shouldValidateValidLoginRequestWithEmailAndPassword() {
			// Given
			LogInDTO login = new LogInDTO();
			login.setEmail("john.doe@example.com");
			login.setPassword("password123");
			// When
			Boolean result = validator.checkRequestLogin(login);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should accept valid login request with Google token")
		void shouldValidateValidLoginRequestWithGoogleToken() {
			// Given
			LogInDTO login = new LogInDTO();
			login.setGoogleToken("valid.google.token");
			// When
			Boolean result = validator.checkRequestLogin(login);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null login request")
		void shouldRejectNullLoginRequest() {
			// When
			Boolean result = validator.checkRequestLogin(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject login request with invalid credentials")
		void shouldRejectLoginRequestWithInvalidCredentials() {
			// Given
			LogInDTO login = new LogInDTO();
			login.setEmail("invalid.email");
			login.setPassword("");
			// When
			Boolean result = validator.checkRequestLogin(login);
			// Then
			assertFalse(result);
		}
	}

	/** Token payload validation tests */
	@Nested
	@DisplayName("Token payload validation tests")
	class TokenValidationTests {

		@Test
		@DisplayName("Should accept valid token payload")
		void shouldValidateValidToken() {
			// Given
			TokenDTO token = new TokenDTO();
			token.setToken("valid.jwt.token");
			// When
			Boolean result = validator.checkRequestToken(token);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null token")
		void shouldRejectNullToken() {
			// When
			Boolean result = validator.checkRequestToken(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject empty token string")
		void shouldRejectEmptyToken() {
			// Given
			TokenDTO token = new TokenDTO();
			token.setToken("");
			// When
			Boolean result = validator.checkRequestToken(token);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject token DTO with null token field")
		void shouldRejectTokenWithNullValue() {
			// Given
			TokenDTO token = new TokenDTO();
			token.setToken(null);
			// When
			Boolean result = validator.checkRequestToken(token);
			// Then
			assertFalse(result);
		}
	}

	/** Place payload validation tests */
	@Nested
	@DisplayName("Place payload validation tests")
	class PlaceValidationTests {

		@Test
		@DisplayName("Should accept valid place payload")
		void shouldValidateValidPlace() {
			// Given
			PlaceDTO place = new PlaceDTO();
			place.setType(EntityType.EAT);
			place.setTimes("10:00-22:00");
			place.setLatitude(45.0);
			place.setLongitude(2.0);
			place.setTitle("Restaurant");
			place.setRating(4.5f);
			place.setDescription("A good restaurant");
			place.setVisibility(true);
			// When
			Boolean result = validator.checkPlace(place);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null place")
		void shouldRejectNullPlace() {
			// When
			Boolean result = validator.checkPlace(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject place with negative id")
		void shouldRejectPlaceWithNegativeId() {
			// Given
			PlaceDTO place = new PlaceDTO();
			place.setId(-1);
			place.setType(EntityType.EAT);
			place.setTimes("10:00-22:00");
			place.setLatitude(45.0);
			place.setLongitude(2.0);
			place.setTitle("Restaurant");
			place.setVisibility(true);
			// When
			Boolean result = validator.checkPlace(place);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject place with invalid latitude")
		void shouldRejectPlaceWithInvalidLatitude() {
			// Given
			PlaceDTO place = new PlaceDTO();
			place.setType(EntityType.EAT);
			place.setTimes("10:00-22:00");
			place.setLatitude(91.0); // Out of range
			place.setLongitude(2.0);
			place.setTitle("Restaurant");
			place.setVisibility(true);
			// When
			Boolean result = validator.checkPlace(place);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject place with invalid longitude")
		void shouldRejectPlaceWithInvalidLongitude() {
			// Given
			PlaceDTO place = new PlaceDTO();
			place.setType(EntityType.EAT);
			place.setTimes("10:00-22:00");
			place.setLatitude(45.0);
			place.setLongitude(181.0); // Out of range
			place.setTitle("Restaurant");
			place.setVisibility(true);
			// When
			Boolean result = validator.checkPlace(place);
			// Then
			assertFalse(result);
		}
	}

	/** Placelist payload validation tests */
	@Nested
	@DisplayName("Placelist payload validation tests")
	class PlacelistValidationTests {

		@Test
		@DisplayName("Should accept valid placelist payload")
		void shouldValidateValidPlacelist() {
			// Given
			PlacelistDTO placelist = new PlacelistDTO();
			placelist.setType(EntityType.EAT);
			placelist.setTitle("Restaurants");
			placelist.setRating(4.5f);
			placelist.setDescription("Restaurant list");
			placelist.setVisibility(true);
			// When
			Boolean result = validator.checkPlacelist(placelist);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null placelist")
		void shouldRejectNullPlacelist() {
			// When
			Boolean result = validator.checkPlacelist(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject placelist with negative id")
		void shouldRejectPlacelistWithNegativeId() {
			// Given
			PlacelistDTO placelist = new PlacelistDTO();
			placelist.setId(-1);
			placelist.setType(EntityType.EAT);
			placelist.setTitle("Restaurants");
			placelist.setVisibility(true);
			// When
			Boolean result = validator.checkPlacelist(placelist);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject placelist with title over max length")
		void shouldRejectPlacelistWithTooLongTitle() {
			// Given
			PlacelistDTO placelist = new PlacelistDTO();
			placelist.setType(EntityType.EAT);
			placelist.setTitle("x".repeat(31)); // 31 characters
			placelist.setVisibility(true);
			// When
			Boolean result = validator.checkPlacelist(placelist);
			// Then
			assertFalse(result);
		}
	}

	/** Comment payload validation tests */
	@Nested
	@DisplayName("Comment payload validation tests")
	class CommentValidationTests {

		@Test
		@DisplayName("Should accept valid comment payload")
		void shouldValidateValidComment() {
			// Given
			CommentDTO comment = new CommentDTO();
			comment.setRating(4.5f);
			comment.setMessage("Great spot!");
			// When
			Boolean result = validator.checkComment(comment);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null comment")
		void shouldRejectNullComment() {
			// When
			Boolean result = validator.checkComment(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject comment with invalid rating")
		void shouldRejectCommentWithInvalidRating() {
			// Given
			CommentDTO comment = new CommentDTO();
			comment.setRating(5.5f); // Out of range
			comment.setMessage("Great spot!");
			// When
			Boolean result = validator.checkComment(comment);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject comment with message over max length")
		void shouldRejectCommentWithTooLongMessage() {
			// Given
			CommentDTO comment = new CommentDTO();
			comment.setRating(4.5f);
			comment.setMessage("x".repeat(1001)); // 1001 characters
			// When
			Boolean result = validator.checkComment(comment);
			// Then
			assertFalse(result);
		}
	}

	/** Photo payload validation tests */
	@Nested
	@DisplayName("Photo payload validation tests")
	class PhotoValidationTests {

		@Test
		@DisplayName("Should accept valid photo payload")
		void shouldValidateValidPhoto() {
			// Given
			PhotoDTO photo = new PhotoDTO();
			photo.setPlaceId(1);
			photo.setUrl("http://example.com/photo.jpg");
			photo.setDescription("A nice photo");
			// When
			Boolean result = validator.checkPhoto(photo);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null photo")
		void shouldRejectNullPhoto() {
			// When
			Boolean result = validator.checkPhoto(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject photo with URL over max length")
		void shouldRejectPhotoWithTooLongUrl() {
			// Given
			PhotoDTO photo = new PhotoDTO();
			photo.setUrl("x".repeat(1001)); // URL longer than 1000 characters
			photo.setDescription("A nice photo");
			// When
			Boolean result = validator.checkPhoto(photo);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject photo with description over max length")
		void shouldRejectPhotoWithTooLongDescription() {
			// Given
			PhotoDTO photo = new PhotoDTO();
			photo.setUrl("http://example.com/photo.jpg");
			photo.setDescription("x".repeat(501)); // Description longer than 500 characters
			// When
			Boolean result = validator.checkPhoto(photo);
			// Then
			assertFalse(result);
		}
	}

	/** Area search validation tests */
	@Nested
	@DisplayName("Area search validation tests")
	class AreaSearchValidationTests {

		@Test
		@DisplayName("Should accept valid bounding box")
		void shouldValidateValidAreaSearch() {
			// Given
			Double fromLat = 45.0;
			Double toLat = 46.0;
			Double fromLong = 2.0;
			Double toLong = 3.0;
			// When
			Boolean result = validator.checkAreaSearch(fromLat, toLat, fromLong, toLong);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject area search when any coordinate is null")
		void shouldRejectAreaSearchWithNullCoordinates() {
			// When
			Boolean result = validator.checkAreaSearch(null, 46.0, 2.0, 3.0);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject area search with invalid latitude")
		void shouldRejectAreaSearchWithInvalidLatitude() {
			// Given
			Double fromLat = -91.0; // Out of range
			Double toLat = 46.0;
			Double fromLong = 2.0;
			Double toLong = 3.0;
			// When
			Boolean result = validator.checkAreaSearch(fromLat, toLat, fromLong, toLong);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject area search with invalid longitude")
		void shouldRejectAreaSearchWithInvalidLongitude() {
			// Given
			Double fromLat = 45.0;
			Double toLat = 46.0;
			Double fromLong = 2.0;
			Double toLong = 181.0; // Out of range
			// When
			Boolean result = validator.checkAreaSearch(fromLat, toLat, fromLong, toLong);
			// Then
			assertFalse(result);
		}
	}

	/** Identifier validation tests */
	@Nested
	@DisplayName("Identifier validation tests")
	class IdValidationTests {

		@Test
		@DisplayName("Should accept positive id")
		void shouldValidateValidId() {
			// Given
			Integer id = 1;
			// When
			Boolean result = validator.checkID(id);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should reject null id")
		void shouldRejectNullId() {
			// When
			Boolean result = validator.checkID(null);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject negative id")
		void shouldRejectNegativeId() {
			// Given
			Integer id = -1;
			// When
			Boolean result = validator.checkID(id);
			// Then
			assertFalse(result);
		}

		@Test
		@DisplayName("Should reject zero id")
		void shouldRejectZeroId() {
			// Given
			Integer id = 0;
			// When
			Boolean result = validator.checkID(id);
			// Then
			assertFalse(result);
		}
	}
}
