package fr.stp_ws.data.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;

/**
 * Loading service tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Loading service tests")
class LoadingServiceTest {

	private LoadingService loadingService;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// Initialize loading service
		loadingService = new LoadingService();
	}

	/** Place association loading tests */
	@Nested
	@DisplayName("Place association loading tests")
	class PlaceAssociationsTests {

		@Test
		@DisplayName("Should not load comments with NONE mode")
		void shouldNotLoadCommentsWithNoneMode() {
			// Given
			Place place = mock(Place.class);
			// When
			loadingService.loadPlaceAssociations(place, CommentMode.NONE, PhotoMode.MAX_3);
			// Then
			verify(place, never()).getComments();
		}

		@Test
		@DisplayName("Should not load photos with NONE mode")
		void shouldNotLoadPhotosWithNoneMode() {
			// Given
			Place place = mock(Place.class);
			// When
			loadingService.loadPlaceAssociations(place, CommentMode.MAX_3, PhotoMode.NONE);
			// Then
			verify(place, never()).getPhotos();
		}

		@Test
		@DisplayName("Should do nothing with a null place")
		void shouldDoNothingWithNullPlace() {
			// When & Then
			assertDoesNotThrow(() -> loadingService.loadPlaceAssociations(null, CommentMode.MAX_3, PhotoMode.MAX_3));
		}
	}

	/** Placelist association loading tests */
	@Nested
	@DisplayName("Placelist association loading tests")
	class PlacelistAssociationsTests {

		@Test
		@DisplayName("Should not load places with WITHOUT_PLACES mode")
		void shouldNotLoadPlacesWithoutPlacesMode() {
			// Given
			Placelist placelist = mock(Placelist.class);
			// When
			loadingService.loadPlacelistAssociations(placelist, PlacelistMode.WITHOUT_PLACES, CommentMode.MAX_3);
			// Then
			verify(placelist, never()).getPlaces();
		}

		@Test
		@DisplayName("Should not load comments with NONE mode")
		void shouldNotLoadCommentsWithNoneMode() {
			// Given
			Placelist placelist = mock(Placelist.class);
			// When
			loadingService.loadPlacelistAssociations(placelist, PlacelistMode.WITH_PLACES, CommentMode.NONE);
			// Then
			verify(placelist, never()).getComments();
		}

		@Test
		@DisplayName("Should do nothing with a null placelist")
		void shouldDoNothingWithNullPlacelist() {
			// When & Then
			assertDoesNotThrow(
					() -> loadingService.loadPlacelistAssociations(null, PlacelistMode.WITH_PLACES, CommentMode.MAX_3));
		}
	}

	/** Comment association loading tests */
	@Nested
	@DisplayName("Comment association loading tests")
	class CommentAssociationsTests {

		@Test
		@DisplayName("Should do nothing with a null comment")
		void shouldDoNothingWithNullComment() {
			// When & Then
			assertDoesNotThrow(() -> loadingService.loadCommentAssociations(null));
		}
	}

	/** Photo association loading tests */
	@Nested
	@DisplayName("Photo association loading tests")
	class PhotoAssociationsTests {

		@Test
		@DisplayName("Should do nothing with a null photo")
		void shouldDoNothingWithNullPhoto() {
			// When & Then
			assertDoesNotThrow(() -> loadingService.loadPhotoAssociations(null));
		}
	}
}
