package fr.stp_ws.presentation.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.application.usecase.inter.IPlacelistUC;
import fr.stp_ws.domain.exception.AlreadyAssociatedException;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.MaxPlaceByPlacelistException;
import fr.stp_ws.domain.exception.MaxPlacelistException;
import fr.stp_ws.domain.exception.NotAssociatedException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import fr.stp_ws.presentation.endpoint.impl.PlacelistEndpoint;
import fr.stp_ws.presentation.exception.NotAuthorizedException;
import fr.stp_ws.presentation.validator.RequestValidator;
import jakarta.ws.rs.core.SecurityContext;

/**
 * Placelist endpoint tests
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Placelist endpoint tests")
class PlacelistEndpointTest {

	private static final Integer USER_ID = 1;
	private PlacelistEndpoint placelistEndpoint;
	private IPlacelistUC placelistUC;
	private RequestValidator validator;
	private SecurityContext securityContext;

	/** Before each test */
	@BeforeEach
	void setUp() throws Exception {
		// Wire mocks into endpoint
		placelistUC = mock(IPlacelistUC.class);
		validator = mock(RequestValidator.class);
		securityContext = mock(SecurityContext.class);
		Principal principal = () -> String.valueOf(USER_ID);
		when(securityContext.getUserPrincipal()).thenReturn(principal);
		placelistEndpoint = new PlacelistEndpoint(placelistUC);

		Field securityContextField = PlacelistEndpoint.class.getDeclaredField("securityContext");
		securityContextField.setAccessible(true);
		securityContextField.set(placelistEndpoint, securityContext);
	}

	/** Authentication context tests */
	@Nested
	@DisplayName("Authentication context tests")
	class AuthenticationContextTests {

		@Test
		@DisplayName("Should throw not authorized when principal ID is invalid")
		void shouldThrowNotAuthorizedWhenPrincipalIdIsInvalid() {
			// Given
			Principal invalidPrincipal = () -> "not-a-number";
			when(securityContext.getUserPrincipal()).thenReturn(invalidPrincipal);
			// When / Then
			NotAuthorizedException naex = assertThrows(NotAuthorizedException.class,
					() -> placelistEndpoint.getPlacelists("ALL", false, "eat"));
			assertNotNull(naex.getMessage());
		}
	}

	/** List placelists tests */
	@Nested
	@DisplayName("List placelists tests")
	class GetPlacelistsTests {

		@Test
		@DisplayName("Should return merged user and tourist placelists for ALL category")
		void shouldGetAllPlacelistsSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			String category = "ALL";
			Boolean mine = false;
			String type = "eat";
			List<PlacelistDTO> userPlacelists = Arrays.asList(new PlacelistDTO(), new PlacelistDTO());
			List<PlacelistDTO> touristPlacelists = Arrays.asList(new PlacelistDTO(), new PlacelistDTO());
			when(placelistUC.getAll(EntityCategory.USER, mine, USER_ID, EntityType.EAT)).thenReturn(userPlacelists);
			when(placelistUC.getAll(EntityCategory.TOURIST, mine, USER_ID, EntityType.EAT))
					.thenReturn(touristPlacelists);
			// When
			List<PlacelistDTO> response = placelistEndpoint.getPlacelists(category, mine, type);
			// Then
			assertNotNull(response);
			assertEquals(4, response.size());
			verify(placelistUC).getAll(EntityCategory.USER, mine, USER_ID, EntityType.EAT);
			verify(placelistUC).getAll(EntityCategory.TOURIST, mine, USER_ID, EntityType.EAT);
		}
	}

	/** Get single placelist tests */
	@Nested
	@DisplayName("Get single placelist tests")
	class GetPlacelistTests {

		@Test
		@DisplayName("Should get placelist successfully")
		void shouldGetPlacelistSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			PlacelistDTO expectedPlacelist = new PlacelistDTO();
			expectedPlacelist.setId(placelistId);

			when(validator.checkID(placelistId)).thenReturn(true);
			when(placelistUC.get(placelistId, USER_ID, PlacelistMode.WITH_PLACES, CommentMode.MAX_3))
					.thenReturn(expectedPlacelist);
			// When
			PlacelistDTO response = placelistEndpoint.getPlacelist(validator, placelistId);
			// Then
			assertNotNull(response);
			assertEquals(placelistId, response.getId());
		}

		@Test
		@DisplayName("Should throw when id is invalid")
		void shouldReturnErrorWhenIdIsInvalid() {
			// Given
			Integer invalidId = -1;
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.getPlacelist(validator, invalidId));
			assertNotNull(fex.getMessage());
		}
	}

	/** Count owner placelists endpoint tests */
	@Nested
	@DisplayName("Count owner placelists endpoint tests")
	class CountOwnerPlacelistsEndpointTests {

		@Test
		@DisplayName("Should return count DTO from use case")
		void shouldReturnCountFromUseCase() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(3);
			when(placelistUC.countOwnerPlacelists(USER_ID)).thenReturn(expected);
			CountDTO response = placelistEndpoint.countOwnerPlacelists();
			assertNotNull(response);
			assertEquals(3, response.getCount());
			verify(placelistUC).countOwnerPlacelists(USER_ID);
		}
	}

	/** Add placelist tests */
	@Nested
	@DisplayName("Add placelist tests")
	class AddPlacelistTests {

		@Test
		@DisplayName("Should add placelist successfully")
		void shouldAddPlacelistSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			PlacelistDTO placelistToAdd = new PlacelistDTO();
			placelistToAdd.setTitle("Test Placelist");
			PlacelistDTO expectedPlacelist = new PlacelistDTO();
			expectedPlacelist.setId(1);
			expectedPlacelist.setTitle(placelistToAdd.getTitle());
			when(validator.checkPlacelist(placelistToAdd)).thenReturn(true);
			when(placelistUC.add(placelistToAdd, USER_ID)).thenReturn(expectedPlacelist);
			// When
			PlacelistDTO response = placelistEndpoint.addPlacelist(validator, placelistToAdd);
			// Then
			assertNotNull(response);
			assertEquals(expectedPlacelist.getId(), response.getId());
			assertEquals(expectedPlacelist.getTitle(), response.getTitle());
		}

		@Test
		@DisplayName("Should throw when placelist payload is invalid")
		void shouldReturnErrorWhenPlacelistIsInvalid() {
			// Given
			PlacelistDTO invalidPlacelist = new PlacelistDTO();
			when(validator.checkPlacelist(invalidPlacelist)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.addPlacelist(validator, invalidPlacelist));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should propagate max placelist limit exception")
		void shouldPropagateMaxPlacelistLimitException() throws FunctionalException, TechnicalException {
			// Given
			PlacelistDTO placelistToAdd = new PlacelistDTO();
			when(validator.checkPlacelist(placelistToAdd)).thenReturn(true);
			when(placelistUC.add(placelistToAdd, USER_ID))
					.thenThrow(new MaxPlacelistException("Max placelist limit reached"));
			// When / Then
			MaxPlacelistException mplx = assertThrows(MaxPlacelistException.class,
					() -> placelistEndpoint.addPlacelist(validator, placelistToAdd));
			assertNotNull(mplx.getMessage());
		}
	}

	/** Update placelist tests */
	@Nested
	@DisplayName("Update placelist tests")
	class UpdatePlacelistTests {

		@Test
		@DisplayName("Should update placelist successfully")
		void shouldUpdatePlacelistSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			PlacelistDTO placelistToUpdate = new PlacelistDTO();
			placelistToUpdate.setId(1);
			placelistToUpdate.setTitle("Updated Placelist");
			when(validator.checkPlacelist(placelistToUpdate)).thenReturn(true);
			when(placelistUC.update(placelistToUpdate, USER_ID)).thenReturn(placelistToUpdate);
			// When
			PlacelistDTO response = placelistEndpoint.updatePlacelist(validator, placelistToUpdate);
			// Then
			assertNotNull(response);
			assertEquals(placelistToUpdate.getId(), response.getId());
			assertEquals(placelistToUpdate.getTitle(), response.getTitle());
		}

		@Test
		@DisplayName("Should throw when placelist payload is invalid")
		void shouldReturnErrorWhenPlacelistIsInvalid() {
			// Given
			PlacelistDTO invalidPlacelist = new PlacelistDTO();
			when(validator.checkPlacelist(invalidPlacelist)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.updatePlacelist(validator, invalidPlacelist));
			assertNotNull(fex.getMessage());
		}
	}

	/** Delete placelist tests */
	@Nested
	@DisplayName("Delete placelist tests")
	class DeletePlacelistTests {

		@Test
		@DisplayName("Should delete placelist successfully")
		void shouldDeletePlacelistSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			PlacelistDTO deletedPlacelist = new PlacelistDTO();
			deletedPlacelist.setId(placelistId);
			when(validator.checkID(placelistId)).thenReturn(true);
			when(placelistUC.delete(placelistId, USER_ID)).thenReturn(deletedPlacelist);
			// When
			PlacelistDTO response = placelistEndpoint.deletePlacelist(validator, placelistId);
			// Then
			assertNotNull(response);
			assertEquals(placelistId, response.getId());
		}

		@Test
		@DisplayName("Should throw when id is invalid")
		void shouldReturnErrorWhenIdIsInvalid() {
			// Given
			Integer invalidId = -1;
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.deletePlacelist(validator, invalidId));
			assertNotNull(fex.getMessage());
		}
	}

	/** Placelist comments endpoint tests */
	@Nested
	@DisplayName("Placelist comments endpoint tests")
	class PlacelistCommentEndpointTests {

		@Test
		@DisplayName("Should get all comments for placelist")
		void shouldGetAllCommentsSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			List<CommentDTO> expectedComments = Arrays.asList(new CommentDTO(), new CommentDTO());
			when(validator.checkID(placelistId)).thenReturn(true);
			when(placelistUC.getComments(placelistId, USER_ID)).thenReturn(expectedComments);
			// When
			List<CommentDTO> response = placelistEndpoint.getComments(validator, placelistId);
			// Then
			assertNotNull(response);
			assertEquals(expectedComments.size(), response.size());
		}

		@Test
		@DisplayName("Should throw when fetching comments with invalid placelist id")
		void shouldReturnErrorWhenGettingCommentsWithInvalidId() {
			// Given
			Integer invalidId = -1;
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.getComments(validator, invalidId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should add comment to placelist")
		void shouldAddCommentSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			CommentDTO commentToAdd = new CommentDTO();
			commentToAdd.setMessage("Test comment");
			commentToAdd.setRating(4.5f);
			CommentDTO expectedComment = new CommentDTO();
			expectedComment.setId(1);
			expectedComment.setMessage(commentToAdd.getMessage());
			expectedComment.setRating(commentToAdd.getRating());
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkComment(commentToAdd)).thenReturn(true);
			when(placelistUC.addComment(placelistId, commentToAdd, USER_ID)).thenReturn(expectedComment);
			// When
			CommentDTO response = placelistEndpoint.addComment(validator, placelistId, commentToAdd);
			// Then
			assertNotNull(response);
			assertEquals(expectedComment.getId(), response.getId());
			assertEquals(expectedComment.getMessage(), response.getMessage());
			assertEquals(expectedComment.getRating(), response.getRating());
		}

		@Test
		@DisplayName("Should throw when adding comment with invalid placelist id")
		void shouldReturnErrorWhenAddingCommentWithInvalidId() {
			// Given
			Integer invalidId = -1;
			CommentDTO comment = new CommentDTO();
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.addComment(validator, invalidId, comment));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when comment payload is invalid")
		void shouldReturnErrorWhenAddingInvalidComment() {
			// Given
			Integer placelistId = 1;
			CommentDTO invalidComment = new CommentDTO();
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkComment(invalidComment)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.addComment(validator, placelistId, invalidComment));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should delete placelist comment successfully")
		void shouldDeleteCommentSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			CommentDTO commentToDelete = new CommentDTO();
			commentToDelete.setId(1);
			CommentDTO expectedComment = new CommentDTO();
			expectedComment.setId(commentToDelete.getId());
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkComment(commentToDelete)).thenReturn(true);
			when(placelistUC.deleteComment(placelistId, commentToDelete.getId(), USER_ID)).thenReturn(expectedComment);
			// When
			CommentDTO response = placelistEndpoint.deleteComment(validator, placelistId, commentToDelete);
			// Then
			assertNotNull(response);
			assertEquals(expectedComment.getId(), response.getId());
		}

		@Test
		@DisplayName("Should throw when deleting comment with invalid placelist id")
		void shouldReturnErrorWhenDeletingCommentWithInvalidId() {
			// Given
			Integer invalidId = -1;
			CommentDTO comment = new CommentDTO();
			when(validator.checkID(invalidId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.deleteComment(validator, invalidId, comment));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when comment payload is invalid for delete")
		void shouldReturnErrorWhenDeletingInvalidComment() {
			// Given
			Integer placelistId = 1;
			CommentDTO invalidComment = new CommentDTO();
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkComment(invalidComment)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.deleteComment(validator, placelistId, invalidComment));
			assertNotNull(fex.getMessage());
		}
	}

	/** Count owner comment placelist endpoint tests */
	@Nested
	@DisplayName("Count owner comment endpoint tests")
	class CountOwnerCommentEndpointTests {

		@Test
		@DisplayName("Should return comment count for placelist")
		void shouldReturnCommentCountSuccessfully() throws FunctionalException, TechnicalException {
			Integer placelistId = 1;
			CountDTO expected = new CountDTO();
			expected.setCount(1);
			when(validator.checkID(placelistId)).thenReturn(true);
			when(placelistUC.countOwnerComment(placelistId, USER_ID)).thenReturn(expected);
			CountDTO response = placelistEndpoint.countOwnerComment(validator, placelistId);
			assertNotNull(response);
			assertEquals(1, response.getCount());
			verify(placelistUC).countOwnerComment(placelistId, USER_ID);
		}

		@Test
		@DisplayName("Should throw when placelist id invalid for comment count")
		void shouldThrowWhenPlacelistIdInvalidForCommentCount() {
			when(validator.checkID(-1)).thenReturn(false);
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.countOwnerComment(validator, -1));
			assertNotNull(fex.getMessage());
		}
	}

	/** Placelist places endpoint tests */
	@Nested
	@DisplayName("Placelist places endpoint tests")
	class PlacelistPlaceManagementEndpointTests {

		@Test
		@DisplayName("Should return place count for placelist")
		void shouldReturnPlaceCountSuccessfully() throws FunctionalException, TechnicalException {
			Integer placelistId = 1;
			CountDTO expected = new CountDTO();
			expected.setCount(6);
			when(validator.checkID(placelistId)).thenReturn(true);
			when(placelistUC.countPlacesInPlacelist(placelistId, USER_ID)).thenReturn(expected);
			CountDTO response = placelistEndpoint.countPlacesInPlacelist(validator, placelistId);
			assertNotNull(response);
			assertEquals(6, response.getCount());
			verify(placelistUC).countPlacesInPlacelist(placelistId, USER_ID);
		}

		@Test
		@DisplayName("Should throw when placelist id invalid for place count")
		void shouldThrowWhenPlacelistIdInvalidForPlaceCount() {
			when(validator.checkID(-1)).thenReturn(false);
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.countPlacesInPlacelist(validator, -1));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should add place to placelist successfully")
		void shouldAddPlaceSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			Integer placeId = 1;
			PlaceDTO expectedPlace = new PlaceDTO();
			expectedPlace.setId(placeId);
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placelistUC.addPlace(placelistId, placeId, USER_ID)).thenReturn(expectedPlace);
			// When
			PlaceDTO response = placelistEndpoint.addPlaceToPlacelist(validator, placelistId, placeId);
			// Then
			assertNotNull(response);
			assertEquals(placeId, response.getId());
		}

		@Test
		@DisplayName("Should throw when place is already associated with placelist")
		void shouldReturnErrorWhenAddingAlreadyAssociatedPlace() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			Integer placeId = 1;
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placelistUC.addPlace(placelistId, placeId, USER_ID))
					.thenThrow(new AlreadyAssociatedException("Place is already in the placelist"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.addPlaceToPlacelist(validator, placelistId, placeId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should propagate max places by placelist exception")
		void shouldPropagateMaxPlacesByPlacelistException() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			Integer placeId = 1;
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placelistUC.addPlace(placelistId, placeId, USER_ID))
					.thenThrow(new MaxPlaceByPlacelistException("Placelist reached max places"));
			// When / Then
			MaxPlaceByPlacelistException mpbpex = assertThrows(MaxPlaceByPlacelistException.class,
					() -> placelistEndpoint.addPlaceToPlacelist(validator, placelistId, placeId));
			assertNotNull(mpbpex.getMessage());
		}

		@Test
		@DisplayName("Should throw when adding place with invalid placelist id")
		void shouldReturnErrorWhenAddingPlaceWithInvalidPlacelistId() {
			// Given
			Integer invalidPlacelistId = -1;
			Integer placeId = 1;
			when(validator.checkID(invalidPlacelistId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.addPlaceToPlacelist(validator, invalidPlacelistId, placeId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when adding place with invalid place id")
		void shouldReturnErrorWhenAddingPlaceWithInvalidPlaceId() {
			// Given
			Integer placelistId = 1;
			Integer invalidPlaceId = -1;
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkID(invalidPlaceId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.addPlaceToPlacelist(validator, placelistId, invalidPlaceId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should remove place from placelist successfully")
		void shouldRemovePlaceFromPlacelistSuccessfully() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			Integer placeId = 1;
			PlaceDTO expectedPlace = new PlaceDTO();
			expectedPlace.setId(placeId);
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placelistUC.removePlace(placelistId, placeId, USER_ID)).thenReturn(expectedPlace);
			// When
			PlaceDTO response = placelistEndpoint.removePlaceFromPlacelist(validator, placelistId, placeId);
			// Then
			assertNotNull(response);
			assertEquals(placeId, response.getId());
		}

		@Test
		@DisplayName("Should throw when removing place not associated with placelist")
		void shouldReturnErrorWhenRemovingNotAssociatedPlace() throws FunctionalException, TechnicalException {
			// Given
			Integer placelistId = 1;
			Integer placeId = 1;
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkID(placeId)).thenReturn(true);
			when(placelistUC.removePlace(placelistId, placeId, USER_ID))
					.thenThrow(new NotAssociatedException("Place is not in the placelist"));
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.removePlaceFromPlacelist(validator, placelistId, placeId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when removing place with invalid placelist id")
		void shouldReturnErrorWhenRemovingPlaceWithInvalidPlacelistId() {
			// Given
			Integer invalidPlacelistId = -1;
			Integer placeId = 1;
			when(validator.checkID(invalidPlacelistId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.removePlaceFromPlacelist(validator, invalidPlacelistId, placeId));
			assertNotNull(fex.getMessage());
		}

		@Test
		@DisplayName("Should throw when removing place with invalid place id")
		void shouldReturnErrorWhenRemovingPlaceWithInvalidPlaceId() {
			// Given
			Integer placelistId = 1;
			Integer invalidPlaceId = -1;
			when(validator.checkID(placelistId)).thenReturn(true);
			when(validator.checkID(invalidPlaceId)).thenReturn(false);
			// When & Then
			FunctionalException fex = assertThrows(FunctionalException.class,
					() -> placelistEndpoint.removePlaceFromPlacelist(validator, placelistId, invalidPlaceId));
			assertNotNull(fex.getMessage());
		}
	}
}
