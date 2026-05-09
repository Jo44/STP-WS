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
import fr.stp_ws.application.service.impl.PlacelistService;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.PlacelistUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyCommentedPlacelistException;
import fr.stp_ws.domain.exception.MaxPlaceByPlacelistException;
import fr.stp_ws.domain.exception.MaxPlacelistException;
import fr.stp_ws.domain.exception.RestrictedAccessException;

/**
 * Placelist service tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Placelist service tests")
@ExtendWith(MockitoExtension.class)
class PlacelistServiceTest {

	private PlacelistService placelistService;
	private User testUser;
	private Placelist testPlacelist;
	private Place testPlace;
	private CommentPlacelist testComment;

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
		placelistService = new PlacelistService(limitsProvider);
		// User initialization
		testUser = new User();
		testUser.setId(1);
		testUser.setTourist(false);
		// Placelist initialization
		testPlacelist = new PlacelistUser();
		testPlacelist.setId(1);
		testPlacelist.setOwner(testUser);
		testPlacelist.setVisibility(true);
		testPlacelist.setPlaces(new ArrayList<>());
		testPlacelist.setComments(new ArrayList<>());
		// Place initialization
		testPlace = new PlaceUser();
		testPlace.setId(1);
		testPlace.setOwner(testUser);
		// Comment initialization
		testComment = new CommentPlacelist();
		testComment.setId(1);
		testComment.setOwner(testUser);
		testComment.setPlacelist(testPlacelist);
	}

	/** Ownership checks tests */
	@Nested
	@DisplayName("Ownership checks tests")
	class IsOwnerTests {

		@Test
		@DisplayName("Should return true when user is the owner")
		void shouldReturnTrueWhenUserIsOwner() {
			// When
			boolean result = placelistService.isOwner(testUser, testPlacelist);
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
			boolean result = placelistService.isOwner(otherUser, testPlacelist);
			// Then
			assertFalse(result);
		}
	}

	/** Visibility checks tests */
	@Nested
	@DisplayName("Visibility checks tests")
	class IsVisibleTests {

		@Test
		@DisplayName("Should return true when placelist is visible")
		void shouldReturnTrueWhenPlacelistIsVisible() {
			// When
			boolean result = placelistService.isVisible(testPlacelist);
			// Then
			assertTrue(result);
		}

		@Test
		@DisplayName("Should return false when placelist is not visible")
		void shouldReturnFalseWhenPlacelistIsNotVisible() {
			// Given
			PlacelistUser invisiblePlacelist = new PlacelistUser();
			invisiblePlacelist.setId(1);
			invisiblePlacelist.setOwner(testUser);
			invisiblePlacelist.setVisibility(false);
			// When
			boolean result = placelistService.isVisible(invisiblePlacelist);
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
			placelistService.canGet(testUser, testPlacelist);
		}

		@Test
		@DisplayName("Should not throw when placelist is visible")
		void shouldNotThrowWhenPlacelistIsVisible() throws RestrictedAccessException {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When & Then
			placelistService.canGet(otherUser, testPlacelist);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner and"
				+ " placelist is not visible")
		void shouldThrowWhenUserIsNotOwnerAndPlacelistIsNotVisible() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			PlacelistUser invisiblePlacelist = new PlacelistUser();
			invisiblePlacelist.setId(1);
			invisiblePlacelist.setOwner(testUser);
			invisiblePlacelist.setVisibility(false);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canGet(otherUser, invisiblePlacelist));
			assertNotNull(raex.getMessage());
		}
	}

	/** Add permission tests */
	@Nested
	@DisplayName("Add permission tests")
	class CanAddTests {

		@Test
		@DisplayName("Should not throw when user is the owner and placelist count" + " is below the limit")
		void shouldNotThrowWhenUserIsOwnerAndBelowLimit() {
			assertDoesNotThrow(() -> placelistService.canAdd(testUser, testPlacelist, 2));
		}

		@Test
		@DisplayName("Should throw MaxPlacelistException when user is the owner and"
				+ " placelist count is above the limit")
		void shouldThrowMaxPlacelistExceptionWhenUserIsOwnerAndAboveLimit() {
			MaxPlacelistException mpex = assertThrows(MaxPlacelistException.class,
					() -> placelistService.canAdd(testUser, testPlacelist, 6));
			assertNotNull(mpex.getMessage());
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowRestrictedAccessExceptionWhenUserIsNotOwner() {
			User otherUser = new User();
			otherUser.setId(2);
			otherUser.setTourist(false);
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canAdd(otherUser, testPlacelist, 2));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should not throw when tourist office user is the owner and placelist count"
				+ " is below the limit")
		void shouldNotThrowWhenTouristOfficeAndBelowLimit() {
			testUser.setTourist(true);
			assertDoesNotThrow(() -> placelistService.canAdd(testUser, testPlacelist, 15));
		}

		@Test
		@DisplayName("Should throw MaxPlacelistException when tourist office user is the owner and"
				+ " placelist count is above the limit")
		void shouldThrowMaxPlacelistExceptionWhenTouristOfficeAndAboveLimit() {
			testUser.setTourist(true);
			MaxPlacelistException mpex = assertThrows(MaxPlacelistException.class,
					() -> placelistService.canAdd(testUser, testPlacelist, 21));
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
			placelistService.canUpdate(testUser, testPlacelist);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowWhenUserIsNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canUpdate(otherUser, testPlacelist));
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
			placelistService.canDelete(testUser, testPlacelist);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowWhenUserIsNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canDelete(otherUser, testPlacelist));
			assertNotNull(raex.getMessage());
		}
	}

	/** Add comment permission tests */
	@Nested
	@DisplayName("Add comment permission tests")
	class CanAddCommentTests {

		@Test
		@DisplayName("Should not throw when user is allowed to comment")
		void shouldNotThrowWhenUserCanComment() throws AlreadyCommentedPlacelistException, RestrictedAccessException {
			// When & Then
			placelistService.canAddComment(testUser, testPlacelist, false);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when placelist is not visible and"
				+ " user is not the owner")
		void shouldThrowWhenPlacelistNotVisibleAndUserNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			PlacelistUser invisiblePlacelist = new PlacelistUser();
			invisiblePlacelist.setId(1);
			invisiblePlacelist.setOwner(testUser);
			invisiblePlacelist.setVisibility(false);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canAddComment(otherUser, invisiblePlacelist, false));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should throw AlreadyCommentedPlacelistException when user has already commented")
		void shouldThrowWhenUserAlreadyCommented() {
			// When & Then
			AlreadyCommentedPlacelistException acpex = assertThrows(AlreadyCommentedPlacelistException.class,
					() -> placelistService.canAddComment(testUser, testPlacelist, true));
			assertNotNull(acpex.getMessage());
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
			placelistService.canDeleteComment(testUser, testPlacelist, testComment);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when placelist is not visible and"
				+ " user is not the owner")
		void shouldThrowWhenPlacelistNotVisibleAndUserNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			PlacelistUser invisiblePlacelist = new PlacelistUser();
			invisiblePlacelist.setId(1);
			invisiblePlacelist.setOwner(testUser);
			invisiblePlacelist.setVisibility(false);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canDeleteComment(otherUser, invisiblePlacelist, testComment));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the comment owner")
		void shouldThrowWhenUserIsNotCommentOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			CommentPlacelist otherUserComment = new CommentPlacelist();
			otherUserComment.setId(2);
			otherUserComment.setOwner(otherUser);
			otherUserComment.setPlacelist(testPlacelist);
			otherUserComment.setMessage("Other user's comment");
			otherUserComment.setRating(3.5f);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canDeleteComment(testUser, testPlacelist, otherUserComment));
			assertNotNull(raex.getMessage());
		}
	}

	/** Add place to placelist permission tests */
	@Nested
	@DisplayName("Add place to placelist permission tests")
	class CanAddPlaceTests {

		@Test
		@DisplayName("Should not throw when user is the owner and place count" + " is below the limit")
		void shouldNotThrowWhenUserIsOwnerAndBelowLimit() {
			assertDoesNotThrow(() -> placelistService.canAddPlace(testUser, testPlacelist, testPlace, 5));
		}

		@Test
		@DisplayName("Should throw MaxPlaceByPlacelistException when user is the owner and"
				+ " place count is above the limit")
		void shouldThrowMaxPlaceByPlacelistExceptionWhenUserIsOwnerAndAboveLimit() {
			MaxPlaceByPlacelistException mpbpex = assertThrows(MaxPlaceByPlacelistException.class,
					() -> placelistService.canAddPlace(testUser, testPlacelist, testPlace, 11));
			assertNotNull(mpbpex.getMessage());
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowRestrictedAccessExceptionWhenUserIsNotOwner() {
			User otherUser = new User();
			otherUser.setId(2);
			otherUser.setTourist(false);
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canAddPlace(otherUser, testPlacelist, testPlace, 5));
			assertNotNull(raex.getMessage());
		}

		@Test
		@DisplayName("Should not throw when tourist office user is the owner and place count" + " is below the limit")
		void shouldNotThrowWhenTouristOfficeAndBelowLimit() {
			testUser.setTourist(true);
			assertDoesNotThrow(() -> placelistService.canAddPlace(testUser, testPlacelist, testPlace, 50));
		}

		@Test
		@DisplayName("Should throw MaxPlaceByPlacelistException when tourist office user is the owner"
				+ " and place count is above the limit")
		void shouldThrowMaxPlaceByPlacelistExceptionWhenTouristOfficeAndAboveLimit() {
			testUser.setTourist(true);
			MaxPlaceByPlacelistException mpbpex = assertThrows(MaxPlaceByPlacelistException.class,
					() -> placelistService.canAddPlace(testUser, testPlacelist, testPlace, 101));
			assertNotNull(mpbpex.getMessage());
		}
	}

	/** Remove place from placelist permission tests */
	@Nested
	@DisplayName("Remove place from placelist permission tests")
	class CanRemovePlaceTests {

		@Test
		@DisplayName("Should not throw when user is the owner")
		void shouldNotThrowWhenUserIsOwner() throws RestrictedAccessException {
			// When & Then
			placelistService.canRemovePlace(testUser, testPlacelist);
		}

		@Test
		@DisplayName("Should throw RestrictedAccessException when user is not the owner")
		void shouldThrowWhenUserIsNotOwner() {
			// Given
			User otherUser = new User();
			otherUser.setId(2);
			// When & Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistService.canRemovePlace(otherUser, testPlacelist));
			assertNotNull(raex.getMessage());
		}
	}
}
