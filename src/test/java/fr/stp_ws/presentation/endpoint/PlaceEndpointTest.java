package fr.stp_ws.presentation.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.application.usecase.inter.IPlaceUC;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.MaxPhotoException;
import fr.stp_ws.domain.exception.MaxPlaceException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.presentation.endpoint.impl.PlaceEndpoint;
import fr.stp_ws.presentation.exception.InvalidRequestException;
import fr.stp_ws.presentation.exception.NotAuthorizedException;
import fr.stp_ws.presentation.validator.RequestValidator;
import jakarta.ws.rs.core.SecurityContext;

/**
 * Place endpoint tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Place endpoint tests")
class PlaceEndpointTest {

	private PlaceEndpoint placeEndpoint;
	private IPlaceUC placeUC;
	private RequestValidator validator;
	private SecurityContext securityContext;

	/** Before each test */
	@BeforeEach
	void setUp() throws Exception {
		// Wire mocks into endpoint
		placeUC = mock(IPlaceUC.class);
		validator = mock(RequestValidator.class);
		securityContext = mock(SecurityContext.class);
		Principal principal = () -> "1";
		when(securityContext.getUserPrincipal()).thenReturn(principal);
		placeEndpoint = new PlaceEndpoint(placeUC);

		Field securityContextField = PlaceEndpoint.class.getDeclaredField("securityContext");
		securityContextField.setAccessible(true);
		securityContextField.set(placeEndpoint, securityContext);
	}

	/** List places with area filter tests */
	@Nested
	@DisplayName("List places with area filter tests")
	class GetPlacesTests {

		@Test
		@DisplayName("Should return merged user and tourist places for ALL category")
		void shouldGetAllPlacesSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			String category = "ALL";
			Boolean mine = false;
			String type = "eat";
			Double fromLat = 45.0;
			Double toLat = 46.0;
			Double fromLong = 4.0;
			Double toLong = 5.0;
			when(validator.checkAreaSearch(fromLat, toLat, fromLong, toLong)).thenReturn(true);
			List<PlaceDTO> userPlaces = Arrays.asList(new PlaceDTO(), new PlaceDTO());
			List<PlaceDTO> touristPlaces = Arrays.asList(new PlaceDTO(), new PlaceDTO());
			when(placeUC.getAll(EntityCategory.USER, mine, 1, EntityType.EAT, fromLat, toLat, fromLong, toLong))
					.thenReturn(userPlaces);
			when(placeUC.getAll(EntityCategory.TOURIST, mine, 1, EntityType.EAT, fromLat, toLat, fromLong, toLong))
					.thenReturn(touristPlaces);
			// When
			List<PlaceDTO> response = placeEndpoint.getPlaces(validator, category, mine, type, fromLat, toLat, fromLong,
					toLong);
			// Then
			assertNotNull(response);
			assertEquals(4, response.size());
			verify(placeUC).getAll(EntityCategory.USER, mine, 1, EntityType.EAT, fromLat, toLat, fromLong, toLong);
			verify(placeUC).getAll(EntityCategory.TOURIST, mine, 1, EntityType.EAT, fromLat, toLat, fromLong, toLong);
		}

		@Test
		@DisplayName("Should throw invalid request exception when area search parameters are invalid")
		void shouldReturnErrorWhenPositionParametersAreInvalid() {
			// Given
			String category = "ALL";
			Boolean mine = false;
			String type = "eat";
			Double fromLat = 91.0; // Invalid latitude
			Double toLat = 46.0;
			Double fromLong = 4.0;
			Double toLong = 5.0;
			when(validator.checkAreaSearch(fromLat, toLat, fromLong, toLong)).thenReturn(false);
			// When & Then
			InvalidRequestException ex = assertThrows(InvalidRequestException.class,
					() -> placeEndpoint.getPlaces(validator, category, mine, type, fromLat, toLat, fromLong, toLong));
			assertNotNull(ex.getMessage());
		}
	}

	@Nested
	@DisplayName("Authentication context tests")
	class AuthenticationContextTests {

		@Test
		@DisplayName("Should throw not authorized when principal is missing")
		void shouldThrowNotAuthorizedWhenPrincipalMissing() throws Exception {
			// Given
			when(securityContext.getUserPrincipal()).thenReturn(null);
			when(validator.checkID(1)).thenReturn(true);
			// When / Then
			NotAuthorizedException naex = assertThrows(NotAuthorizedException.class,
					() -> placeEndpoint.getPlace(validator, 1));
			assertNotNull(naex.getMessage());
		}
	}

	/** Get single place tests */
	@Nested
	@DisplayName("Get single place tests")
	class GetPlaceTests {

		@Test
		@DisplayName("Should get place successfully")
		void shouldGetPlaceSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			PlaceDTO expectedPlace = new PlaceDTO();
			expectedPlace.setId(placeId);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placeUC.get(placeId, 1, CommentMode.MAX_3, PhotoMode.MAX_3)).thenReturn(expectedPlace);
			// When
			PlaceDTO response = placeEndpoint.getPlace(validator, placeId);
			// Then
			assertNotNull(response);
			assertEquals(placeId, response.getId());
			verify(placeUC).get(placeId, 1, CommentMode.MAX_3, PhotoMode.MAX_3);
		}

		@Test
		@DisplayName("Should throw when id is invalid")
		void shouldReturnErrorWhenIdIsInvalid() {
			// Given
			Integer placeId = -1;
			when(validator.checkID(placeId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.getPlace(validator, placeId));
			assertNotNull(fex.getMessage());
		}
	}

	/** Add place tests */
	@Nested
	@DisplayName("Add place tests")
	class AddPlaceTests {

		@Test
		@DisplayName("Should add place successfully")
		void shouldAddPlaceSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			PlaceDTO placeToAdd = new PlaceDTO();
			placeToAdd.setTitle("Test Place");
			PlaceDTO expectedPlace = new PlaceDTO();
			expectedPlace.setId(1);
			expectedPlace.setTitle("Test Place");
			when(validator.checkPlace(placeToAdd)).thenReturn(true);
			when(placeUC.add(placeToAdd, 1)).thenReturn(expectedPlace);
			// When
			PlaceDTO response = placeEndpoint.addPlace(validator, placeToAdd);
			// Then
			assertNotNull(response);
			assertEquals(expectedPlace.getId(), response.getId());
			assertEquals(expectedPlace.getTitle(), response.getTitle());
			verify(placeUC).add(placeToAdd, 1);
		}

		@Test
		@DisplayName("Should throw when place payload is invalid")
		void shouldReturnErrorWhenPlaceIsInvalid() {
			// Given
			PlaceDTO invalidPlace = new PlaceDTO();
			when(validator.checkPlace(invalidPlace)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.addPlace(validator, invalidPlace));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should propagate max place limit exception")
		void shouldPropagateMaxPlaceLimitException() throws FunctionalException, TechnicalException {
			// Given
			PlaceDTO placeToAdd = new PlaceDTO();
			when(validator.checkPlace(placeToAdd)).thenReturn(true);
			when(placeUC.add(placeToAdd, 1)).thenThrow(new MaxPlaceException("Max place limit reached"));
			// When / Then
			MaxPlaceException mpex = assertThrows(MaxPlaceException.class,
					() -> placeEndpoint.addPlace(validator, placeToAdd));
			assertNotNull(mpex.getMessage());
		}
	}

	/** Update place tests */
	@Nested
	@DisplayName("Update place tests")
	class UpdatePlaceTests {

		@Test
		@DisplayName("Should update place successfully")
		void shouldUpdatePlaceSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			PlaceDTO placeToUpdate = new PlaceDTO();
			placeToUpdate.setId(1);
			placeToUpdate.setTitle("Updated Place");
			when(validator.checkPlace(placeToUpdate)).thenReturn(true);
			when(placeUC.update(placeToUpdate, 1)).thenReturn(placeToUpdate);
			// When
			PlaceDTO response = placeEndpoint.updatePlace(validator, placeToUpdate);
			// Then
			assertNotNull(response);
			assertEquals(placeToUpdate.getId(), response.getId());
			assertEquals(placeToUpdate.getTitle(), response.getTitle());
			verify(placeUC).update(placeToUpdate, 1);
		}

		@Test
		@DisplayName("Should throw when place payload is invalid")
		void shouldReturnErrorWhenPlaceIsInvalid() {
			// Given
			PlaceDTO invalidPlace = new PlaceDTO();
			when(validator.checkPlace(invalidPlace)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.updatePlace(validator, invalidPlace));
			assertNotNull(fex.getMessage());
		}
	}

	/** Delete place tests */
	@Nested
	@DisplayName("Delete place tests")
	class DeletePlaceTests {

		@Test
		@DisplayName("Should delete place successfully")
		void shouldDeletePlaceSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			PlaceDTO deletedPlace = new PlaceDTO();
			deletedPlace.setId(placeId);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placeUC.delete(placeId, 1)).thenReturn(deletedPlace);
			// When
			PlaceDTO response = placeEndpoint.deletePlace(validator, placeId);
			// Then
			assertNotNull(response);
			assertEquals(placeId, response.getId());
			verify(placeUC).delete(placeId, 1);
		}

		@Test
		@DisplayName("Should throw when id is invalid")
		void shouldReturnErrorWhenIdIsInvalid() {
			// Given
			Integer placeId = -1;
			when(validator.checkID(placeId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.deletePlace(validator, placeId));
			assertNotNull(fex.getMessage());
		}
	}

	/** Place comments endpoint tests */
	@Nested
	@DisplayName("Place comments endpoint tests")
	class PlaceCommentEndpointTests {

		@Test
		@DisplayName("Should get all comments for place")
		void shouldGetAllCommentsSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			List<CommentDTO> expectedComments = Arrays.asList(new CommentDTO(), new CommentDTO());
			when(validator.checkID(placeId)).thenReturn(true);
			when(placeUC.getComments(placeId, 1)).thenReturn(expectedComments);
			// When
			List<CommentDTO> response = placeEndpoint.getComments(validator, placeId);
			// Then
			assertNotNull(response);
			assertEquals(expectedComments.size(), response.size());
			verify(placeUC).getComments(placeId, 1);
		}

		@Test
		@DisplayName("Should throw when fetching comments with invalid place id")
		void shouldReturnErrorWhenGettingCommentsWithInvalidId() {
			// Given
			Integer placeId = -1;
			when(validator.checkID(placeId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.getComments(validator, placeId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should add comment to place")
		void shouldAddCommentSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			CommentDTO commentToAdd = new CommentDTO();
			commentToAdd.setMessage("Test comment");
			commentToAdd.setRating(4.5f);
			CommentDTO expectedComment = new CommentDTO();
			expectedComment.setId(1);
			expectedComment.setMessage(commentToAdd.getMessage());
			expectedComment.setRating(commentToAdd.getRating());
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkComment(commentToAdd)).thenReturn(true);
			when(placeUC.addComment(placeId, commentToAdd, 1)).thenReturn(expectedComment);
			// When
			CommentDTO response = placeEndpoint.addComment(validator, placeId, commentToAdd);
			// Then
			assertNotNull(response);
			assertEquals(expectedComment.getId(), response.getId());
			assertEquals(expectedComment.getMessage(), response.getMessage());
			assertEquals(expectedComment.getRating(), response.getRating());
			verify(placeUC).addComment(placeId, commentToAdd, 1);
		}

		@Test
		@DisplayName("Should throw when adding comment with invalid place id")
		void shouldReturnErrorWhenAddingCommentWithInvalidId() {
			// Given
			Integer placeId = -1;
			CommentDTO comment = new CommentDTO();
			when(validator.checkID(placeId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.addComment(validator, placeId, comment));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when comment payload is invalid")
		void shouldReturnErrorWhenAddingInvalidComment() {
			// Given
			Integer placeId = 1;
			CommentDTO invalidComment = new CommentDTO();
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkComment(invalidComment)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.addComment(validator, placeId, invalidComment));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should delete place comment successfully")
		void shouldDeleteCommentSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			CommentDTO commentToDelete = new CommentDTO();
			commentToDelete.setId(1);
			CommentDTO expectedComment = new CommentDTO();
			expectedComment.setId(commentToDelete.getId());
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkComment(commentToDelete)).thenReturn(true);
			when(placeUC.deleteComment(placeId, commentToDelete.getId(), 1)).thenReturn(expectedComment);
			// When
			CommentDTO response = placeEndpoint.deleteComment(validator, placeId, commentToDelete);
			// Then
			assertNotNull(response);
			assertEquals(expectedComment.getId(), response.getId());
			verify(placeUC).deleteComment(placeId, commentToDelete.getId(), 1);
		}

		@Test
		@DisplayName("Should throw when deleting comment with invalid place id")
		void shouldReturnErrorWhenDeletingCommentWithInvalidId() {
			// Given
			Integer placeId = -1;
			CommentDTO comment = new CommentDTO();
			when(validator.checkID(placeId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.deleteComment(validator, placeId, comment));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when comment payload is invalid for delete")
		void shouldReturnErrorWhenDeletingInvalidComment() {
			// Given
			Integer placeId = 1;
			CommentDTO invalidComment = new CommentDTO();
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkComment(invalidComment)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.deleteComment(validator, placeId, invalidComment));
			assertNotNull(fex.getMessage());
		}
	}

	/** Place photos endpoint tests */
	@Nested
	@DisplayName("Place photos endpoint tests")
	class PlacePhotoEndpointTests {

		@Test
		@DisplayName("Should get all photos for place successfully")
		void shouldGetAllPhotosSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			List<PhotoDTO> expectedPhotos = new ArrayList<>();
			PhotoDTO photo = new PhotoDTO();
			photo.setId(1);
			photo.setUrl("test.jpg");
			photo.setDescription("Test photo");
			photo.setUploadDate(new Timestamp(System.currentTimeMillis()));
			expectedPhotos.add(photo);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placeUC.getPhotos(placeId, 1)).thenReturn(expectedPhotos);
			// When
			List<PhotoDTO> response = placeEndpoint.getPhotos(validator, placeId);
			// Then
			assertNotNull(response);
			assertEquals(1, response.size());
			assertEquals(photo.getId(), response.get(0).getId());
			assertEquals(photo.getUrl(), response.get(0).getUrl());
			assertEquals(photo.getDescription(), response.get(0).getDescription());
			assertEquals(photo.getUploadDate(), response.get(0).getUploadDate());
		}

		@Test
		@DisplayName("Should throw when fetching photos with invalid place id")
		void shouldReturnErrorWhenGettingPhotosWithInvalidId() {
			// Given
			Integer invalidId = -1;
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.getPhotos(validator, invalidId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should add photo successfully")
		void shouldAddPhotoSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			PhotoDTO photo = new PhotoDTO();
			photo.setId(1);
			photo.setUrl("test.jpg");
			photo.setDescription("Test photo");
			photo.setUploadDate(new Timestamp(System.currentTimeMillis()));
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkPhoto(photo)).thenReturn(true);
			when(placeUC.addPhoto(placeId, photo, 1)).thenReturn(photo);
			// When
			PhotoDTO response = placeEndpoint.addPhoto(validator, placeId, photo);
			// Then
			assertNotNull(response);
			assertEquals(photo.getId(), response.getId());
			assertEquals(photo.getUrl(), response.getUrl());
			assertEquals(photo.getDescription(), response.getDescription());
			assertEquals(photo.getUploadDate(), response.getUploadDate());
		}

		@Test
		@DisplayName("Should throw when adding photo with invalid place id")
		void shouldReturnErrorWhenAddingPhotoWithInvalidId() {
			// Given
			Integer invalidId = -1;
			PhotoDTO photo = new PhotoDTO();
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.addPhoto(validator, invalidId, photo));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when photo payload is invalid")
		void shouldReturnErrorWhenAddingInvalidPhoto() {
			// Given
			Integer placeId = 1;
			PhotoDTO invalidPhoto = new PhotoDTO();
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkPhoto(invalidPhoto)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.addPhoto(validator, placeId, invalidPhoto));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should propagate max photo limit exception")
		void shouldPropagateMaxPhotoLimitException() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			PhotoDTO photo = new PhotoDTO();
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkPhoto(photo)).thenReturn(true);
			when(placeUC.addPhoto(placeId, photo, 1)).thenThrow(new MaxPhotoException("Max photo limit reached"));
			// When / Then
			MaxPhotoException mphex = assertThrows(MaxPhotoException.class,
					() -> placeEndpoint.addPhoto(validator, placeId, photo));
			assertNotNull(mphex.getMessage());
		}

		@Test
		@DisplayName("Should delete photo successfully")
		void shouldDeletePhotoSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placeId = 1;
			PhotoDTO photo = new PhotoDTO();
			photo.setId(1);
			photo.setUrl("test.jpg");
			photo.setDescription("Test photo");
			photo.setUploadDate(new Timestamp(System.currentTimeMillis()));
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkPhoto(photo)).thenReturn(true);
			when(placeUC.deletePhoto(placeId, photo.getId(), 1)).thenReturn(photo);
			// When
			PhotoDTO response = placeEndpoint.deletePhoto(validator, placeId, photo);
			// Then
			assertNotNull(response);
			assertEquals(photo.getId(), response.getId());
			assertEquals(photo.getUrl(), response.getUrl());
			assertEquals(photo.getDescription(), response.getDescription());
			assertEquals(photo.getUploadDate(), response.getUploadDate());
		}

		@Test
		@DisplayName("Should throw when deleting photo with invalid place id")
		void shouldReturnErrorWhenDeletingPhotoWithInvalidId() {
			// Given
			Integer invalidId = -1;
			PhotoDTO photo = new PhotoDTO();
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.deletePhoto(validator, invalidId, photo));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when photo payload is invalid for delete")
		void shouldReturnErrorWhenDeletingInvalidPhoto() {
			// Given
			Integer placeId = 1;
			PhotoDTO invalidPhoto = new PhotoDTO();
			when(validator.checkID(placeId)).thenReturn(true);
			when(validator.checkPhoto(invalidPhoto)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placeEndpoint.deletePhoto(validator, placeId, invalidPhoto));
			assertNotNull(fex.getMessage());
		}
	}
}
