package fr.stp_ws.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import fr.stp_ws.application.model.mapper.inter.IBasicPlaceMapper;
import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.ICountMapper;
import fr.stp_ws.application.model.mapper.inter.IPhotoMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.repository.ICommentRepo;
import fr.stp_ws.application.repository.IPhotoRepo;
import fr.stp_ws.application.repository.IPlaceRepo;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.service.inter.IPlaceService;
import fr.stp_ws.application.usecase.impl.PlaceUC;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistPhotoException;
import fr.stp_ws.domain.exception.RestrictedAccessException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;

/**
 * Place use-cases tests
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Place use-cases tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PlaceUCTest {

	@InjectMocks
	private PlaceUC placeUC;
	@Mock
	private IUserRepo userRepo;
	@Mock
	private IPlaceRepo placeRepo;
	@Mock
	private ICommentRepo commentRepo;
	@Mock
	private IPhotoRepo photoRepo;
	@Mock
	private IPlaceMapper placeMapper;
	@Mock
	private IBasicPlaceMapper basicPlaceMapper;
	@Mock
	private ICommentMapper commentMapper;
	@Mock
	private IPhotoMapper photoMapper;
	@Mock
	private ICountMapper countMapper;
	@Mock
	private IPlaceService placeService;
	private User testUser;
	private PlaceUser testPlace;
	private CommentPlace testComment;
	private Photo testPhoto;
	private PlaceDTO testPlaceDTO;
	private CommentDTO testCommentDTO;
	private PhotoDTO testPhotoDTO;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		testUser = new User();
		testUser.setId(1);
		// Place initialization
		testPlace = new PlaceUser();
		testPlace.setId(1);
		testPlace.setOwner(testUser);
		testPlace.setType(EntityType.EAT);
		testPlace.setTimes("10:00-22:00");
		testPlace.setLatitude(48.8566);
		testPlace.setLongitude(2.3522);
		testPlace.setTitle("Test Place");
		testPlace.setRating(4.5f);
		testPlace.setDescription("Test Description");
		testPlace.setVisibility(true);
		testPlace.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testPlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		// Comment initialization
		testComment = new CommentPlace();
		testComment.setId(1);
		testComment.setOwner(testUser);
		testComment.setMessage("Test comment");
		testComment.setRating(4.5f);
		testComment.setPlace(testPlace);
		// Photo initialization
		testPhoto = new Photo();
		testPhoto.setId(1);
		testPhoto.setUrl("test-photo.jpg");
		testPhoto.setPlace(testPlace);
		// Place DTO initialization
		testPlaceDTO = new PlaceDTO();
		// Comment DTO initialization
		testCommentDTO = new CommentDTO();
		testCommentDTO.setMessage("Test comment");
		testCommentDTO.setRating(4.5f);
		// Photo DTO initialization
		testPhotoDTO = new PhotoDTO();
		testPhotoDTO.setUrl("test-photo.jpg");
	}

	/** Get all places tests */
	@Nested
	@DisplayName("Get all places tests")
	class GetAllPlacesTests {

		@Test
		@DisplayName("Should return all places for category ALL")
		void shouldReturnAllPlacesForCategoryAll() throws FunctionalException, TechnicalException {
			// Given
			List<Place> userPlaces = new ArrayList<>();
			userPlaces.add(testPlace);
			List<Place> touristPlaces = new ArrayList<>();
			touristPlaces.add(testPlace);
			when(placeRepo.getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL), eq(null), eq(null),
					eq(null), eq(null))).thenReturn(userPlaces);
			when(placeRepo.getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL), eq(null),
					eq(null), eq(null), eq(null))).thenReturn(touristPlaces);
			when(placeMapper.toDTOList(any(), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(List.of(testPlaceDTO));
			// When
			List<PlaceDTO> result = placeUC.getAll(EntityCategory.ALL, false, null, EntityType.ALL, null, null, null,
					null);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(placeRepo, times(1)).getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL),
					eq(null), eq(null), eq(null), eq(null));
			verify(placeRepo, times(1)).getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL),
					eq(null), eq(null), eq(null), eq(null));
			verify(placeMapper, times(1)).toDTOList(any(), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should return user places only")
		void shouldReturnUserPlacesOnly() throws FunctionalException, TechnicalException {
			// Given
			List<Place> userPlaces = new ArrayList<>();
			userPlaces.add(testPlace);
			when(placeRepo.getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL), eq(null), eq(null),
					eq(null), eq(null))).thenReturn(userPlaces);
			when(placeMapper.toDTOList(any(), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(List.of(testPlaceDTO));
			// When
			List<PlaceDTO> result = placeUC.getAll(EntityCategory.USER, false, null, EntityType.ALL, null, null, null,
					null);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(placeRepo, times(1)).getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL),
					eq(null), eq(null), eq(null), eq(null));
			verify(placeMapper, times(1)).toDTOList(any(), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should return tourist places only")
		void shouldReturnTouristPlacesOnly() throws FunctionalException, TechnicalException {
			// Given
			List<Place> touristPlaces = new ArrayList<>();
			touristPlaces.add(testPlace);
			when(placeRepo.getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL), eq(null),
					eq(null), eq(null), eq(null))).thenReturn(touristPlaces);
			when(placeMapper.toDTOList(any(), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(List.of(testPlaceDTO));
			// When
			List<PlaceDTO> result = placeUC.getAll(EntityCategory.TOURIST, false, null, EntityType.ALL, null, null,
					null, null);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(placeRepo, times(1)).getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL),
					eq(null), eq(null), eq(null), eq(null));
			verify(placeMapper, times(1)).toDTOList(any(), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}
	}

	/** Get single place tests */
	@Nested
	@DisplayName("Get single place tests")
	class GetPlaceByIdTests {

		@Test
		@DisplayName("Should return place by ID")
		void shouldReturnPlaceById() throws FunctionalException, TechnicalException {
			// Given
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeMapper.toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(testPlaceDTO);
			// When
			PlaceDTO result = placeUC.get(1, 1, CommentMode.NONE, PhotoMode.NONE);
			// Then
			assertNotNull(result);
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
			verify(placeMapper, times(1)).toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception for invalid ID")
		void shouldThrowExceptionForInvalidId() throws FunctionalException, TechnicalException {
			// Given
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canGet(eq(testUser), eq(null));
			// When/Then
			FunctionalException fex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.get(999, 1, CommentMode.NONE, PhotoMode.NONE));
			assertNotNull(fex.getMessage());
		}
	}

	/** Add place tests */
	@Nested
	@DisplayName("Add place tests")
	class AddPlaceTests {

		@Test
		@DisplayName("Should add new place")
		void shouldAddNewPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlaceMapper.toEntity(eq(testPlaceDTO), eq(testUser))).thenReturn(testPlace);
			when(placeRepo.count(eq(1))).thenReturn(0);
			when(placeRepo.add(eq(testPlace))).thenReturn(testPlace);
			when(placeMapper.toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(testPlaceDTO);
			// When
			PlaceDTO result = placeUC.add(testPlaceDTO, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlaceMapper, times(1)).toEntity(eq(testPlaceDTO), eq(testUser));
			verify(placeRepo, times(1)).count(eq(1));
			verify(placeService, times(1)).canAdd(eq(testUser), eq(testPlace), eq(0));
			verify(placeRepo, times(1)).add(eq(testPlace));
			verify(placeMapper, times(1)).toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when user has reached place limit")
		void shouldThrowExceptionWhenUserHasReachedPlaceLimit() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlaceMapper.toEntity(eq(testPlaceDTO), eq(testUser))).thenReturn(testPlace);
			when(placeRepo.count(eq(1))).thenReturn(20); // Maximum number of places for a regular user
			doThrow(new RestrictedAccessException("User has reached place limit")).when(placeService)
					.canAdd(eq(testUser), eq(testPlace), eq(20));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.add(testPlaceDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlaceMapper, times(1)).toEntity(eq(testPlaceDTO), eq(testUser));
			verify(placeRepo, times(1)).count(eq(1));
			verify(placeService, times(1)).canAdd(eq(testUser), eq(testPlace), eq(20));
		}
	}

	/** Update place tests */
	@Nested
	@DisplayName("Update place tests")
	class UpdatePlaceTests {

		@Test
		@DisplayName("Should update existing place")
		void shouldUpdateExistingPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlaceMapper.toEntity(eq(testPlaceDTO), eq(testUser))).thenReturn(testPlace);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(placeRepo.update(eq(testPlace))).thenReturn(testPlace);
			when(placeMapper.toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(testPlaceDTO);
			// When
			PlaceDTO result = placeUC.update(testPlaceDTO, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlaceMapper, times(1)).toEntity(eq(testPlaceDTO), eq(testUser));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canUpdate(eq(testUser), eq(testPlace));
			verify(placeRepo, times(1)).update(eq(testPlace));
			verify(placeMapper, times(1)).toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when updating non-existent place")
		void shouldThrowExceptionForNonExistentPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlaceMapper.toEntity(eq(testPlaceDTO), eq(testUser))).thenReturn(testPlace);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canUpdate(eq(testUser),
					eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.update(testPlaceDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlaceMapper, times(1)).toEntity(eq(testPlaceDTO), eq(testUser));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canUpdate(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks update permission")
		void shouldThrowExceptionWhenUserLacksUpdatePermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlaceMapper.toEntity(eq(testPlaceDTO), eq(testUser))).thenReturn(testPlace);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("User cannot update this place")).when(placeService)
					.canUpdate(eq(testUser), eq(testPlace));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.update(testPlaceDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlaceMapper, times(1)).toEntity(eq(testPlaceDTO), eq(testUser));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canUpdate(eq(testUser), eq(testPlace));
		}

		@Test
		@DisplayName("Should reject update when user A tries to update user B place")
		void shouldRejectUpdateWhenUserATriesToUpdateUserBPlace() throws FunctionalException, TechnicalException {
			// Given
			User userA = new User();
			userA.setId(1);
			User userB = new User();
			userB.setId(2);
			PlaceUser placeOwnedByB = new PlaceUser();
			placeOwnedByB.setId(1);
			placeOwnedByB.setOwner(userB);
			when(userRepo.getById(eq(1))).thenReturn(userA);
			when(basicPlaceMapper.toEntity(eq(testPlaceDTO), eq(userA))).thenReturn(placeOwnedByB);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(placeOwnedByB);
			doThrow(new RestrictedAccessException("User A cannot update place owned by user B")).when(placeService)
					.canUpdate(eq(userA), eq(placeOwnedByB));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.update(testPlaceDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canUpdate(eq(userA), eq(placeOwnedByB));
		}
	}

	/** Delete place tests */
	@Nested
	@DisplayName("Delete place tests")
	class DeletePlaceTests {

		@Test
		@DisplayName("Should delete existing place")
		void shouldDeleteExistingPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(placeRepo.delete(eq(1))).thenReturn(testPlace);
			when(placeMapper.toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(testPlaceDTO);
			// When
			PlaceDTO result = placeUC.delete(1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canDelete(eq(testUser), eq(testPlace));
			verify(placeRepo, times(1)).delete(eq(1));
			verify(placeMapper, times(1)).toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when deleting non-existent place")
		void shouldThrowExceptionForNonExistentPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canDelete(eq(testUser),
					eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.delete(999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canDelete(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks delete permission")
		void shouldThrowExceptionWhenUserLacksDeletePermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("User cannot delete this place")).when(placeService)
					.canDelete(eq(testUser), eq(testPlace));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class, () -> placeUC.delete(1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canDelete(eq(testUser), eq(testPlace));
		}
	}

	/** Get place comments tests */
	@Nested
	@DisplayName("Get place comments tests")
	class GetPlaceCommentsTests {

		@Test
		@DisplayName("Should return comments for a valid place")
		void shouldReturnCommentsForValidPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			List<Comment> comments = List.of(testComment);
			when(commentRepo.getAll(eq(1), eq(1), eq(Place.class))).thenReturn(comments);
			when(commentMapper.toDTOList(eq(comments))).thenReturn(List.of(testCommentDTO));
			// When
			List<CommentDTO> result = placeUC.getComments(1, 1);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
			verify(commentRepo, times(1)).getAll(eq(1), eq(1), eq(Place.class));
			verify(commentMapper, times(1)).toDTOList(eq(comments));
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canGet(eq(testUser), eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.getComments(999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("User cannot access this place")).when(placeService)
					.canGet(eq(testUser), eq(testPlace));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.getComments(1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
		}

		@Test
		@DisplayName("Should return empty list when place has no comments")
		void shouldReturnEmptyListWhenPlaceHasNoComments() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(commentRepo.getAll(eq(1), eq(1), eq(Place.class))).thenReturn(new ArrayList<>());
			when(commentMapper.toDTOList(any())).thenReturn(new ArrayList<>());
			// When
			List<CommentDTO> result = placeUC.getComments(1, 1);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
			verify(commentRepo, times(1)).getAll(eq(1), eq(1), eq(Place.class));
			verify(commentMapper, times(1)).toDTOList(any());
		}
	}

	/** Add place comment tests */
	@Nested
	@DisplayName("Add place comment tests")
	class AddPlaceCommentTests {

		@Test
		@DisplayName("Should add comment to a valid place")
		void shouldAddCommentToValidPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(commentMapper.toEntity(eq(testCommentDTO), eq(testUser), eq(testPlace), eq(Place.class)))
					.thenReturn(testComment);
			when(commentRepo.add(eq(1), eq(testComment), eq(Place.class))).thenReturn(testComment);
			when(commentMapper.toDTO(eq(testComment))).thenReturn(testCommentDTO);
			// When
			CommentDTO result = placeUC.addComment(1, testCommentDTO, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canAddComment(eq(testUser), eq(testPlace), eq(false));
			verify(commentMapper, times(1)).toEntity(eq(testCommentDTO), eq(testUser), eq(testPlace), eq(Place.class));
			verify(commentRepo, times(1)).add(eq(1), eq(testComment), eq(Place.class));
			verify(commentMapper, times(1)).toDTO(eq(testComment));
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			CommentDTO commentDTO = new CommentDTO();
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canAddComment(eq(testUser),
					eq(null), eq(false));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.addComment(999, commentDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canAddComment(eq(testUser), eq(null), eq(false));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			CommentDTO commentDTO = new CommentDTO();
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("User cannot comment this place")).when(placeService)
					.canAddComment(eq(testUser), eq(testPlace), eq(false));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.addComment(1, commentDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canAddComment(eq(testUser), eq(testPlace), eq(false));
		}
	}

	/** Delete place comment tests */
	@Nested
	@DisplayName("Delete place comment tests")
	class DeletePlaceCommentTests {

		@Test
		@DisplayName("Should delete comment from a valid place")
		void shouldDeleteCommentFromValidPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(commentRepo.get(eq(1), eq(Place.class))).thenReturn(testComment);
			when(commentRepo.delete(eq(1), eq(Place.class))).thenReturn(testComment);
			when(commentMapper.toDTO(eq(testComment))).thenReturn(testCommentDTO);
			// When
			CommentDTO result = placeUC.deleteComment(1, 1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(commentRepo, times(1)).get(eq(1), eq(Place.class));
			verify(placeService, times(1)).canDeleteComment(eq(testUser), eq(testPlace), eq(testComment));
			verify(commentRepo, times(1)).delete(eq(1), eq(Place.class));
			verify(commentMapper, times(1)).toDTO(eq(testComment));
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			when(commentRepo.get(eq(1), eq(Place.class))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canDeleteComment(eq(testUser),
					eq(null), eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.deleteComment(999, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(commentRepo, times(1)).get(eq(1), eq(Place.class));
			verify(placeService, times(1)).canDeleteComment(eq(testUser), eq(null), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when comment does not exist")
		void shouldThrowExceptionWhenCommentDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(commentRepo.get(eq(999), eq(Place.class))).thenReturn(null);
			doThrow(new RestrictedAccessException("Comment not found")).when(placeService)
					.canDeleteComment(eq(testUser), eq(testPlace), eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.deleteComment(1, 999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(commentRepo, times(1)).get(eq(999), eq(Place.class));
			verify(placeService, times(1)).canDeleteComment(eq(testUser), eq(testPlace), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks delete permission")
		void shouldThrowExceptionWhenUserLacksDeletePermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			Comment comment = new CommentPlace();
			comment.setId(1);
			comment.setOwner(testUser);
			comment.setMessage("Test comment");
			comment.setRating(4.5f);
			when(commentRepo.get(eq(1), eq(Place.class))).thenReturn(comment);
			doThrow(new RestrictedAccessException("User cannot delete this comment")).when(placeService)
					.canDeleteComment(eq(testUser), eq(testPlace), eq(comment));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.deleteComment(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(commentRepo, times(1)).get(eq(1), eq(Place.class));
			verify(placeService, times(1)).canDeleteComment(eq(testUser), eq(testPlace), eq(comment));
		}

		@Test
		@DisplayName("Should throw exception when comment is not associated with the place")
		void shouldThrowExceptionWhenCommentIsNotAssociatedWithThePlace()
				throws FunctionalException, TechnicalException {
			// Given
			PlaceUser otherPlace = new PlaceUser();
			otherPlace.setId(2);
			CommentPlace otherComment = new CommentPlace();
			otherComment.setId(1);
			otherComment.setOwner(testUser);
			otherComment.setPlace(otherPlace);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(commentRepo.get(eq(1), eq(Place.class))).thenReturn(otherComment);
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.deleteComment(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(commentRepo, times(1)).get(eq(1), eq(Place.class));
			verify(placeService, times(1)).canDeleteComment(eq(testUser), eq(testPlace), eq(otherComment));
		}
	}

	/** Get place photos tests */
	@Nested
	@DisplayName("Get place photos tests")
	class GetPlacePhotosTests {

		@Test
		@DisplayName("Should return photos for a valid place")
		void shouldReturnPhotosForValidPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			List<Photo> photos = List.of(testPhoto);
			when(photoRepo.getAll(eq(1), eq(1))).thenReturn(photos);
			when(photoMapper.toDTOList(eq(photos))).thenReturn(List.of(testPhotoDTO));
			// When
			List<PhotoDTO> result = placeUC.getPhotos(1, 1);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
			verify(photoRepo, times(1)).getAll(eq(1), eq(1));
			verify(photoMapper, times(1)).toDTOList(eq(photos));
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canGet(eq(testUser), eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.getPhotos(999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("User cannot access this place")).when(placeService)
					.canGet(eq(testUser), eq(testPlace));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.getPhotos(1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
		}

		@Test
		@DisplayName("Should return empty list when place has no photos")
		void shouldReturnEmptyListWhenPlaceHasNoPhotos() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoRepo.getAll(eq(1), eq(1))).thenReturn(new ArrayList<>());
			when(photoMapper.toDTOList(any())).thenReturn(new ArrayList<>());
			// When
			List<PhotoDTO> result = placeUC.getPhotos(1, 1);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
			verify(photoRepo, times(1)).getAll(eq(1), eq(1));
			verify(photoMapper, times(1)).toDTOList(any());
		}
	}

	/** Add place photo tests */
	@Nested
	@DisplayName("Add place photo tests")
	class AddPlacePhotoTests {

		@Test
		@DisplayName("Should add photo to a valid place")
		void shouldAddPhotoToValidPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoMapper.toEntity(eq(testPhotoDTO), eq(testPlace))).thenReturn(testPhoto);
			when(photoRepo.count(eq(1))).thenReturn(0);
			when(photoRepo.add(eq(testPhoto))).thenReturn(testPhoto);
			when(photoMapper.toDTO(eq(testPhoto))).thenReturn(testPhotoDTO);
			// When
			PhotoDTO result = placeUC.addPhoto(1, testPhotoDTO, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(photoRepo, times(1)).count(eq(1));
			verify(placeService, times(1)).canAddPhoto(eq(testUser), eq(testPlace), eq(0));
			verify(photoMapper, times(1)).toEntity(eq(testPhotoDTO), eq(testPlace));
			verify(photoRepo, times(1)).add(eq(testPhoto));
			verify(photoMapper, times(1)).toDTO(eq(testPhoto));
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			PhotoDTO photoDTO = new PhotoDTO();
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canAddPhoto(eq(testUser),
					eq(null), eq(0));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.addPhoto(999, photoDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canAddPhoto(eq(testUser), eq(null), eq(0));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			PhotoDTO photoDTO = new PhotoDTO();
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoRepo.count(eq(1))).thenReturn(0);
			doThrow(new RestrictedAccessException("User cannot add photo to this place")).when(placeService)
					.canAddPhoto(eq(testUser), eq(testPlace), eq(0));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.addPhoto(1, photoDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(photoRepo, times(1)).count(eq(1));
			verify(placeService, times(1)).canAddPhoto(eq(testUser), eq(testPlace), eq(0));
		}
	}

	/** Delete place photo tests */
	@Nested
	@DisplayName("Delete place photo tests")
	class DeletePlacePhotoTests {

		@Test
		@DisplayName("Should delete photo from a valid place")
		void shouldDeletePhotoFromValidPlace() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoRepo.get(eq(1))).thenReturn(testPhoto);
			when(photoRepo.delete(eq(1))).thenReturn(testPhoto);
			when(photoMapper.toDTO(eq(testPhoto))).thenReturn(testPhotoDTO);
			// When
			PhotoDTO result = placeUC.deletePhoto(1, 1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(photoRepo, times(1)).get(eq(1));
			verify(placeService, times(1)).canDeletePhoto(eq(testUser), eq(testPlace));
			verify(photoRepo, times(1)).delete(eq(1));
			verify(photoMapper, times(1)).toDTO(eq(testPhoto));
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placeService).canDeletePhoto(eq(testUser),
					eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.deletePhoto(999, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canDeletePhoto(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when photo does not exist")
		void shouldThrowExceptionWhenPhotoDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoRepo.get(eq(999))).thenThrow(new NotExistPhotoException("Photo does not exist"));
			// When/Then
			NotExistPhotoException nepex = assertThrows(NotExistPhotoException.class,
					() -> placeUC.deletePhoto(1, 999, 1));
			assertNotNull(nepex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(photoRepo, times(1)).get(eq(999));
		}

		@Test
		@DisplayName("Should throw exception when user lacks delete permission")
		void shouldThrowExceptionWhenUserLacksDeletePermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoRepo.get(eq(1))).thenReturn(testPhoto);
			doThrow(new RestrictedAccessException("User cannot delete this photo")).when(placeService)
					.canDeletePhoto(eq(testUser), eq(testPlace));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.deletePhoto(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placeService, times(1)).canDeletePhoto(eq(testUser), eq(testPlace));
		}

		@Test
		@DisplayName("Should throw exception when photo is not associated with the place")
		void shouldThrowExceptionWhenPhotoIsNotAssociatedWithThePlace() throws FunctionalException, TechnicalException {
			// Given
			Place otherPlace = new PlaceUser();
			otherPlace.setId(2);
			testPhoto.setPlace(otherPlace);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoRepo.get(eq(1))).thenReturn(testPhoto);
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placeUC.deletePhoto(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(photoRepo, times(1)).get(eq(1));
		}
	}

	/** Count owner places tests */
	@Nested
	@DisplayName("Count owner places tests")
	class CountOwnerPlacesTests {

		@Test
		@DisplayName("Should return count DTO for owner places")
		void shouldReturnCountDtoForOwnerPlaces() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(4);
			when(placeRepo.count(eq(1))).thenReturn(4);
			when(countMapper.toDTO(eq(4))).thenReturn(expected);
			CountDTO result = placeUC.countOwnerPlaces(1);
			assertNotNull(result);
			assertEquals(4, result.getCount());
			verify(placeRepo, times(1)).count(eq(1));
			verify(countMapper, times(1)).toDTO(eq(4));
		}
	}

	/** Count owner comment tests */
	@Nested
	@DisplayName("Count owner comment tests")
	class CountOwnerCommentTests {

		@Test
		@DisplayName("Should return count of comments authored by requesting user")
		void shouldReturnCountOfCommentsByRequestingUser() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(1);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(commentRepo.getAll(eq(1), eq(1), eq(Place.class))).thenReturn(List.of(testComment));
			when(countMapper.toDTO(eq(1))).thenReturn(expected);
			CountDTO result = placeUC.countOwnerComment(1, 1);
			assertNotNull(result);
			assertEquals(1, result.getCount());
			verify(placeService, times(1)).canGet(eq(testUser), eq(testPlace));
			verify(countMapper, times(1)).toDTO(eq(1));
		}

		@Test
		@DisplayName("Should return zero when only other users commented")
		void shouldReturnZeroWhenOnlyOtherUsersCommented() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(0);
			User other = new User();
			other.setId(2);
			CommentPlace otherComment = new CommentPlace();
			otherComment.setId(2);
			otherComment.setOwner(other);
			otherComment.setPlace(testPlace);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(commentRepo.getAll(eq(1), eq(1), eq(Place.class))).thenReturn(List.of(otherComment));
			when(countMapper.toDTO(eq(0))).thenReturn(expected);
			CountDTO result = placeUC.countOwnerComment(1, 1);
			assertNotNull(result);
			assertEquals(0, result.getCount());
			verify(countMapper, times(1)).toDTO(eq(0));
		}

		@Test
		@DisplayName("Should throw when user cannot access place for comment count")
		void shouldThrowWhenUserCannotAccessPlace() throws FunctionalException, TechnicalException {
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("denied")).when(placeService).canGet(eq(testUser), eq(testPlace));
			assertThrows(RestrictedAccessException.class, () -> placeUC.countOwnerComment(1, 1));
			verify(commentRepo, never()).getAll(any(), any(), any());
		}
	}

	/** Count photos tests */
	@Nested
	@DisplayName("Count photos tests")
	class CountPhotosTests {

		@Test
		@DisplayName("Should return photo count for accessible place")
		void shouldReturnPhotoCountForAccessiblePlace() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(3);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(photoRepo.count(eq(1))).thenReturn(3);
			when(countMapper.toDTO(eq(3))).thenReturn(expected);
			CountDTO result = placeUC.countPhotos(1, 1);
			assertNotNull(result);
			assertEquals(3, result.getCount());
			verify(photoRepo, times(1)).count(eq(1));
			verify(countMapper, times(1)).toDTO(eq(3));
		}

		@Test
		@DisplayName("Should throw when user cannot access place for photo count")
		void shouldThrowWhenUserCannotAccessPlace() throws FunctionalException, TechnicalException {
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("denied")).when(placeService).canGet(eq(testUser), eq(testPlace));
			assertThrows(RestrictedAccessException.class, () -> placeUC.countPhotos(1, 1));
			verify(photoRepo, never()).count(any());
		}
	}
}
