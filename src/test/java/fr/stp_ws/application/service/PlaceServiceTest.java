package fr.stp_ws.application.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.stp_ws.application.config.ILimitsProvider;
import fr.stp_ws.application.service.impl.PlaceService;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceTourist;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyCommentedPlaceException;
import fr.stp_ws.domain.exception.MaxPhotoException;
import fr.stp_ws.domain.exception.MaxPlaceException;
import fr.stp_ws.domain.exception.RestrictedAccessException;

/**
 * Place service tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Place service tests")
@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

	private PlaceService placeService;
	private User testUser;
	private Place testPlace;
	private CommentPlace testComment;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// Authorization service initialization
		ILimitsProvider limitsProvider = new ILimitsProvider() {
			@Override
			public int getUserMaxPlaces() {
				return 20;
			}

			@Override
			public int getTouristMaxPlaces() {
				return 500;
			}

			@Override
			public int getUserMaxPhotosPerPlace() {
				return 5;
			}

			@Override
			public int getTouristMaxPhotosPerPlace() {
				return 20;
			}

			@Override
			public int getUserMaxPlacelists() {
				return 5;
			}

			@Override
			public int getTouristMaxPlacelists() {
				return 20;
			}

			@Override
			public int getUserMaxPlacesPerPlacelist() {
				return 10;
			}

			@Override
			public int getTouristMaxPlacesPerPlacelist() {
				return 100;
			}
		};
		placeService = new PlaceService(limitsProvider);
		// User initialization
		testUser = new User();
		testUser.setId(1);
		testUser.setTourist(false);
		// Place initialization
		testPlace = new PlaceTourist();
		testPlace.setId(1);
		testPlace.setOwner(testUser);
		testPlace.setVisibility(true);
		testPlace.setPhotos(new ArrayList<>());
		testPlace.setComments(new ArrayList<>());
		// Comment initialization
		testComment = new CommentPlace();
		testComment.setId(1);
		testComment.setOwner(testUser);
		testComment.setPlace(testPlace);
	}

	/** Ownership checks tests */
	@Nested
	@DisplayName("Ownership checks tests")
	class IsOwnerTests {

		@Test
		@DisplayName("Should return true when user is the owner")
		void shouldReturnTrueWhenUserIsOwner() {
			// When
			boolean result = placeService.isOwner(testUser, testPlace);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should return false when user is not the owner")
		void shouldReturnFalseWhenUserIsNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When
			boolean result = placeService.isOwner(otherUser, testPlace);
			// Then
			assertFalse(result);
		}
	}

	/** Visibility checks tests */
	@Nested
	@DisplayName("Visibility checks tests")
	class IsVisibleTests {

		@Test
		@DisplayName("Should return true when place is visible")
		void shouldReturnTrueWhenPlaceIsVisible() {
			// When
			boolean result = placeService.isVisible(testPlace);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should return false when place is not visible")
		void shouldReturnFalseWhenPlaceIsNotVisible() {
			// Given
			PlaceUser invisiblePlace = new PlaceUser();
			invisiblePlace.setId(1);
			invisiblePlace.setOwner(testUser);
			invisiblePlace.setVisibility(false);
			// When
			boolean result = placeService.isVisible(invisiblePlace);
			// Then
			assertFalse(result);
		}
	}

	/** Get permission tests */
	@Nested
	@DisplayName("Get permission tests")
	class CanGetTests {

		@Test
		@DisplayName("Should not throw when user is the owner")
		void shouldNotThrowWhenUserIsOwner() throws RestrictedAccessException {
			// When & Then
			placeService.canGet(testUser, testPlace);
		}

		@Test
		@DisplayName("Should not throw when place is visible")
		void shouldNotThrowWhenPlaceIsVisible() throws RestrictedAccessException {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When & Then
			placeService.canGet(otherUser, testPlace);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner and" + " place is not visible")
		void shouldThrowWhenUserIsNotOwnerAndPlaceIsNotVisible() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			PlaceUser invisiblePlace = new PlaceUser();
			invisiblePlace.setId(1);
			invisiblePlace.setOwner(testUser);
			invisiblePlace.setVisibility(false);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canGet(otherUser, invisiblePlace));
			assertNotNull(raex.getMessage());
		}
	}

	/** Add permission tests */
	@Nested
	@DisplayName("Add permission tests")
	class CanAddTests {

		@Test
		@DisplayName("Should not throw when user is the owner and place count" + " is below the limit")
		void shouldNotThrowWhenUserIsOwnerAndBelowLimit() {
			assertDoesNotThrow(() -> placeService.canAdd(testUser, testPlace, 15));
		}

		@Test
		@DisplayName("Should throw MaxPlaceException when user is the owner and place count" + " is above the limit")
		void shouldThrowMaxPlaceExceptionWhenUserIsOwnerAndAboveLimit() {
			MaxPlaceException mpex = assertThrows(MaxPlaceException.class,
					() -> placeService.canAdd(testUser, testPlace, 21));
			assertNotNull(mpex.getMessage());
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowRestrictedAccessExceptionWhenUserIsNotOwner() {
			User otherUser = new User();
			otherUser.setId(2);
			otherUser.setTourist(false);
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canAdd(otherUser, testPlace, 15));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should not throw when tourist office user is the owner and place count" + " is below the limit")
		void shouldNotThrowWhenTouristOfficeAndBelowLimit() {
			testUser.setTourist(true);
			assertDoesNotThrow(() -> placeService.canAdd(testUser, testPlace, 400));
		}

		@Test
		@DisplayName("Should throw MaxPlaceException when tourist office user is the owner and"
				+ " place count is above the limit")
		void shouldThrowMaxPlaceExceptionWhenTouristOfficeAndAboveLimit() {
			testUser.setTourist(true);
			MaxPlaceException mpex = assertThrows(MaxPlaceException.class,
					() -> placeService.canAdd(testUser, testPlace, 501));
			assertNotNull(mpex.getMessage());
		}
	}

	/** Update permission tests */
	@Nested
	@DisplayName("Update permission tests")
	class CanUpdateTests {

		@Test
		@DisplayName("Should not throw when user is the owner")
		void shouldNotThrowWhenUserIsOwner() throws RestrictedAccessException {
			// When & Then
			placeService.canUpdate(testUser, testPlace);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowWhenUserIsNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canUpdate(otherUser, testPlace));
			assertNotNull(raex.getMessage());
		}
	}

	/** Delete permission tests */
	@Nested
	@DisplayName("Delete permission tests")
	class CanDeleteTests {

		@Test
		@DisplayName("Should not throw when user is the owner")
		void shouldNotThrowWhenUserIsOwner() throws RestrictedAccessException {
			// When & Then
			placeService.canDelete(testUser, testPlace);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowWhenUserIsNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canDelete(otherUser, testPlace));
			assertNotNull(raex.getMessage());
		}
	}

	/** Add comment permission tests */
	@Nested
	@DisplayName("Add comment permission tests")
	class CanAddCommentTests {

		@Test
		@DisplayName("Should not throw when user is allowed to comment")
		void shouldNotThrowWhenUserCanComment() throws AlreadyCommentedPlaceException, RestrictedAccessException {
			// When & Then
			placeService.canAddComment(testUser, testPlace, false);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when place is not visible and" + " user is not the owner")
		void shouldThrowWhenPlaceNotVisibleAndUserNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			PlaceUser invisiblePlace = new PlaceUser();
			invisiblePlace.setId(1);
			invisiblePlace.setOwner(testUser);
			invisiblePlace.setVisibility(false);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canAddComment(otherUser, invisiblePlace, false));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should throw AlreadyCommentedPlaceException when user has already commented")
		void shouldThrowWhenUserAlreadyCommented() {
			// When & Then
			AlreadyCommentedPlaceException acex = assertThrows(AlreadyCommentedPlaceException.class,
					() -> placeService.canAddComment(testUser, testPlace, true));
			assertNotNull(acex.getMessage());
		}
	}

	/** Delete comment permission tests */
	@Nested
	@DisplayName("Delete comment permission tests")
	class CanDeleteCommentTests {

		@Test
		@DisplayName("Should not throw when user is the comment owner")
		void shouldNotThrowWhenUserIsCommentOwner() throws RestrictedAccessException {
			// When & Then
			placeService.canDeleteComment(testUser, testPlace, testComment);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when place is not visible and" + " user is not the owner")
		void shouldThrowWhenPlaceNotVisibleAndUserNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			PlaceUser invisiblePlace = new PlaceUser();
			invisiblePlace.setId(1);
			invisiblePlace.setOwner(testUser);
			invisiblePlace.setVisibility(false);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canDeleteComment(otherUser, invisiblePlace, testComment));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the comment owner")
		void shouldThrowWhenUserIsNotCommentOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			CommentPlace otherUserComment = new CommentPlace();
			otherUserComment.setId(2);
			otherUserComment.setOwner(otherUser);
			otherUserComment.setPlace(testPlace);
			otherUserComment.setMessage("Other user's comment");
			otherUserComment.setRating(3.5f);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canDeleteComment(testUser, testPlace, otherUserComment));
			assertNotNull(raex.getMessage());
		}
	}

	/** Add photo permission tests */
	@Nested
	@DisplayName("Add photo permission tests")
	class CanAddPhotoTests {

		@Test
		@DisplayName("Should not throw when user is the owner and photo count" + " is below the limit")
		void shouldNotThrowWhenUserIsOwnerAndBelowLimit() {
			assertDoesNotThrow(() -> placeService.canAddPhoto(testUser, testPlace, 4));
		}

		@Test
		@DisplayName("Should throw MaxPhotoException when user is the owner and photo count" + " is above the limit")
		void shouldThrowMaxPhotoExceptionWhenUserIsOwnerAndAboveLimit() {
			MaxPhotoException mpex = assertThrows(MaxPhotoException.class,
					() -> placeService.canAddPhoto(testUser, testPlace, 6));
			assertNotNull(mpex.getMessage());
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowRestrictedAccessExceptionWhenUserIsNotOwner() {
			User otherUser = new User();
			otherUser.setId(2);
			otherUser.setTourist(false);
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canAddPhoto(otherUser, testPlace, 4));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should not throw when tourist office user is the owner and photo count" + " is below the limit")
		void shouldNotThrowWhenTouristOfficeAndBelowLimit() {
			testUser.setTourist(true);
			assertDoesNotThrow(() -> placeService.canAddPhoto(testUser, testPlace, 15));
		}

		@Test
		@DisplayName("Should throw MaxPhotoException when tourist office user is the owner and"
				+ " photo count is above the limit")
		void shouldThrowMaxPhotoExceptionWhenTouristOfficeAndAboveLimit() {
			testUser.setTourist(true);
			MaxPhotoException mpex = assertThrows(MaxPhotoException.class,
					() -> placeService.canAddPhoto(testUser, testPlace, 21));
			assertNotNull(mpex.getMessage());
		}
	}

	/** Delete photo permission tests */
	@Nested
	@DisplayName("Delete photo permission tests")
	class CanDeletePhotoTests {

		@Test
		@DisplayName("Should not throw when user is the owner")
		void shouldNotThrowWhenUserIsOwner() throws RestrictedAccessException {
			// When & Then
			placeService.canDeletePhoto(testUser, testPlace);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowWhenUserIsNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);

			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeService.canDeletePhoto(otherUser, testPlace));
			assertNotNull(raex.getMessage());
		}
	}
}
