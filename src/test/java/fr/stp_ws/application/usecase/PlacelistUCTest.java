package fr.stp_ws.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import fr.stp_ws.application.model.mapper.inter.IBasicPlacelistMapper;
import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.ICountMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.model.mapper.inter.IPlacelistMapper;
import fr.stp_ws.application.repository.ICommentRepo;
import fr.stp_ws.application.repository.IPlaceRepo;
import fr.stp_ws.application.repository.IPlacelistRepo;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.service.inter.IPlacelistService;
import fr.stp_ws.application.usecase.impl.PlacelistUC;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.PlacelistUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistPlaceException;
import fr.stp_ws.domain.exception.NotExistPlacelistException;
import fr.stp_ws.domain.exception.RestrictedAccessException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;

/**
 * Placelist use-cases tests
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Placelist use-cases tests")
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class PlacelistUCTest {

	@InjectMocks
	private PlacelistUC placelistUC;
	@Mock
	private IUserRepo userRepo;
	@Mock
	private IPlacelistRepo placelistRepo;
	@Mock
	private IPlaceRepo placeRepo;
	@Mock
	private ICommentRepo commentRepo;
	@Mock
	private IPlacelistMapper placelistMapper;
	@Mock
	private IPlaceMapper placeMapper;
	@Mock
	private IBasicPlacelistMapper basicPlacelistMapper;
	@Mock
	private ICommentMapper commentMapper;
	@Mock
	private ICountMapper countMapper;
	@Mock
	private IPlacelistService placelistService;
	private User testUser;
	private PlacelistUser testPlacelist;
	private PlaceUser testPlace;
	private CommentPlacelist testComment;
	private PlacelistDTO testPlacelistDTO;
	private CommentDTO testCommentDTO;
	private List<Placelist> testUserPlacelists;
	private List<Placelist> testTouristPlacelists;
	private List<Comment> testComments;
	private List<PlacelistDTO> testPlacelistDTOs;
	private List<CommentDTO> testCommentDTOs;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		testUser = new User();
		testUser.setId(1);
		// Placelist initialization
		testPlacelist = new PlacelistUser();
		testPlacelist.setId(1);
		testPlacelist.setOwner(testUser);
		testPlacelist.setType(EntityType.EAT);
		testPlacelist.setTitle("Test Placelist");
		testPlacelist.setDescription("Test Description");
		testPlacelist.setVisibility(true);
		testPlacelist.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testPlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		// Comment initialization
		testComment = new CommentPlacelist();
		testComment.setId(1);
		testComment.setMessage("Test comment");
		testComment.setRating(4.5f);
		testComment.setOwner(testUser);
		testComment.setPlacelist(testPlacelist);
		// Comment list initialization
		testComments = new ArrayList<>();
		testComments.add(testComment);
		// Place initialization
		testPlace = new PlaceUser();
		testPlace.setId(1);
		testPlace.setOwner(testUser);
		testPlace.setType(EntityType.EAT);
		testPlace.setTitle("Test Place");
		// Placelist collections initialization
		testUserPlacelists = new ArrayList<>();
		testUserPlacelists.add(testPlacelist);
		testTouristPlacelists = new ArrayList<>();
		testTouristPlacelists.add(testPlacelist);
		// Placelist DTO initialization
		testPlacelistDTO = new PlacelistDTO();
		testPlacelistDTO.setId(1);
		testPlacelistDTO.setTitle("Test Placelist");
		testPlacelistDTO.setDescription("Test Description");
		testPlacelistDTO.setType(EntityType.EAT);
		testPlacelistDTO.setVisibility(true);
		// Comment DTO initialization
		testCommentDTO = new CommentDTO();
		testCommentDTO.setId(1);
		testCommentDTO.setMessage("Test comment");
		testCommentDTO.setRating(4.5f);
		// DTO list initialization
		testPlacelistDTOs = new ArrayList<>();
		testPlacelistDTOs.add(testPlacelistDTO);
		testCommentDTOs = new ArrayList<>();
		testCommentDTOs.add(testCommentDTO);
		// Default mock stubbing
		when(placelistMapper.toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
				.thenReturn(testPlacelistDTO);
		when(commentMapper.toDTO(any(Comment.class))).thenReturn(testCommentDTO);
		when(placelistMapper.toDTOList(any(), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
				.thenReturn(testPlacelistDTOs);
		when(commentMapper.toDTOList(any())).thenReturn(testCommentDTOs);
	}

	/** Get all placelists tests */
	@Nested
	@DisplayName("Get all placelists tests")
	class GetAllPlacelistsTests {

		@Test
		@DisplayName("Should return all placelists for category ALL")
		void shouldReturnAllPlacelistsForCategoryAll() throws FunctionalException, TechnicalException {
			// Given
			when(placelistRepo.getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL)))
					.thenReturn(testUserPlacelists);
			when(placelistRepo.getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL)))
					.thenReturn(testTouristPlacelists);
			// When
			List<PlacelistDTO> result = placelistUC.getAll(EntityCategory.ALL, false, null, EntityType.ALL);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(placelistRepo, times(1)).getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL));
			verify(placelistRepo, times(1)).getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL));
		}

		@Test
		@DisplayName("Should return user placelists only")
		void shouldReturnUserPlacelistsOnly() throws FunctionalException, TechnicalException {
			// Given
			when(placelistRepo.getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL)))
					.thenReturn(testUserPlacelists);
			// When
			List<PlacelistDTO> result = placelistUC.getAll(EntityCategory.USER, false, null, EntityType.ALL);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(placelistRepo, times(1)).getAll(eq(EntityCategory.USER), eq(false), eq(null), eq(EntityType.ALL));
		}

		@Test
		@DisplayName("Should return tourist placelists only")
		void shouldReturnTouristPlacelistsOnly() throws FunctionalException, TechnicalException {
			// Given
			when(placelistRepo.getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL)))
					.thenReturn(testTouristPlacelists);
			// When
			List<PlacelistDTO> result = placelistUC.getAll(EntityCategory.TOURIST, false, null, EntityType.ALL);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			verify(placelistRepo, times(1)).getAll(eq(EntityCategory.TOURIST), eq(false), eq(null), eq(EntityType.ALL));
		}
	}

	/** Get single placelist tests */
	@Nested
	@DisplayName("Get single placelist tests")
	class GetPlacelistByIdTests {

		@Test
		@DisplayName("Should return placelist by ID")
		void shouldReturnPlacelistById() throws FunctionalException, TechnicalException {
			// Given
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistMapper.toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelistDTO);
			// When
			PlacelistDTO result = placelistUC.get(1, 1, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
			// Then
			assertNotNull(result);
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistService, times(1)).canGet(eq(testUser), eq(testPlacelist));
			verify(placelistMapper, times(1)).toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES),
					eq(CommentMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception for invalid ID")
		void shouldThrowExceptionForInvalidId() throws FunctionalException, TechnicalException {
			// Given
			when(placelistRepo.get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE))).thenReturn(null);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			doThrow(new RestrictedAccessException("Placelist not found")).when(placelistService).canGet(eq(testUser),
					eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.get(999, 1, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE));
			assertNotNull(raex.getMessage());
			verify(placelistRepo, times(1)).get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistService, times(1)).canGet(eq(testUser), eq(null));
		}
	}

	/** Add placelist tests */
	@Nested
	@DisplayName("Add placelist tests")
	class AddPlacelistTests {

		@Test
		@DisplayName("Should add new placelist")
		void shouldAddNewPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlacelistMapper.toEntity(eq(testPlacelistDTO), eq(testUser))).thenReturn(testPlacelist);
			when(placelistRepo.count(eq(1))).thenReturn(0);
			when(placelistRepo.add(eq(testPlacelist))).thenReturn(testPlacelist);
			when(placelistMapper.toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelistDTO);
			// When
			PlacelistDTO result = placelistUC.add(testPlacelistDTO, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlacelistMapper, times(1)).toEntity(eq(testPlacelistDTO), eq(testUser));
			verify(placelistRepo, times(1)).count(eq(1));
			verify(placelistService, times(1)).canAdd(eq(testUser), eq(testPlacelist), eq(0));
			verify(placelistRepo, times(1)).add(eq(testPlacelist));
			verify(placelistMapper, times(1)).toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES),
					eq(CommentMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when user has reached placelist limit")
		void shouldThrowExceptionWhenUserHasReachedPlacelistLimit() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlacelistMapper.toEntity(eq(testPlacelistDTO), eq(testUser))).thenReturn(testPlacelist);
			when(placelistRepo.count(eq(1))).thenReturn(20);
			doThrow(new RestrictedAccessException("User has reached placelist limit")).when(placelistService)
					.canAdd(eq(testUser), eq(testPlacelist), eq(20));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.add(testPlacelistDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlacelistMapper, times(1)).toEntity(eq(testPlacelistDTO), eq(testUser));
			verify(placelistRepo, times(1)).count(eq(1));
			verify(placelistService, times(1)).canAdd(eq(testUser), eq(testPlacelist), eq(20));
		}
	}

	/** Update placelist tests */
	@Nested
	@DisplayName("Update placelist tests")
	class UpdatePlacelistTests {

		@Test
		@DisplayName("Should update existing placelist")
		void shouldUpdateExistingPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlacelistMapper.toEntity(eq(testPlacelistDTO), eq(testUser))).thenReturn(testPlacelist);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placelistRepo.update(eq(testPlacelist))).thenReturn(testPlacelist);
			when(placelistMapper.toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelistDTO);
			// When
			PlacelistDTO result = placelistUC.update(testPlacelistDTO, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlacelistMapper, times(1)).toEntity(eq(testPlacelistDTO), eq(testUser));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canUpdate(eq(testUser), eq(testPlacelist));
			verify(placelistRepo, times(1)).update(eq(testPlacelist));
			verify(placelistMapper, times(1)).toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES),
					eq(CommentMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when updating non-existent placelist")
		void shouldThrowExceptionForNonExistentPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlacelistMapper.toEntity(eq(testPlacelistDTO), eq(testUser))).thenReturn(testPlacelist);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Placelist not found")).when(placelistService).canUpdate(eq(testUser),
					eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.update(testPlacelistDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlacelistMapper, times(1)).toEntity(eq(testPlacelistDTO), eq(testUser));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canUpdate(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks update permission")
		void shouldThrowExceptionWhenUserLacksUpdatePermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(basicPlacelistMapper.toEntity(eq(testPlacelistDTO), eq(testUser))).thenReturn(testPlacelist);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			doThrow(new RestrictedAccessException("User cannot update this placelist")).when(placelistService)
					.canUpdate(eq(testUser), eq(testPlacelist));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.update(testPlacelistDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(basicPlacelistMapper, times(1)).toEntity(eq(testPlacelistDTO), eq(testUser));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canUpdate(eq(testUser), eq(testPlacelist));
		}
	}

	/** Delete placelist tests */
	@Nested
	@DisplayName("Delete placelist tests")
	class DeletePlacelistTests {

		@Test
		@DisplayName("Should delete existing placelist")
		void shouldDeleteExistingPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placelistRepo.delete(eq(1))).thenReturn(testPlacelist);
			when(placelistMapper.toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelistDTO);
			// When
			PlacelistDTO result = placelistUC.delete(1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canDelete(eq(testUser), eq(testPlacelist));
			verify(placelistRepo, times(1)).delete(eq(1));
			verify(placelistMapper, times(1)).toDTO(any(Placelist.class), eq(PlacelistMode.WITHOUT_PLACES),
					eq(CommentMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when deleting non-existent placelist")
		void shouldThrowExceptionForNonExistentPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Placelist not found")).when(placelistService).canDelete(eq(testUser),
					eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.delete(999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canDelete(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks delete permission")
		void shouldThrowExceptionWhenUserLacksDeletePermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			doThrow(new RestrictedAccessException("User cannot delete this placelist")).when(placelistService)
					.canDelete(eq(testUser), eq(testPlacelist));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.delete(1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canDelete(eq(testUser), eq(testPlacelist));
		}

		@Test
		@DisplayName("Should reject delete when user A tries to delete user B placelist")
		void shouldRejectDeleteWhenUserATriesToDeleteUserBPlacelist() throws FunctionalException, TechnicalException {
			// Given
			User userA = new User();
			userA.setId(1);
			User userB = new User();
			userB.setId(2);
			PlacelistUser placelistOwnedByB = new PlacelistUser();
			placelistOwnedByB.setId(1);
			placelistOwnedByB.setOwner(userB);
			when(userRepo.getById(eq(1))).thenReturn(userA);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(placelistOwnedByB);
			doThrow(new RestrictedAccessException("User A cannot delete placelist owned by user B"))
					.when(placelistService).canDelete(eq(userA), eq(placelistOwnedByB));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.delete(1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canDelete(eq(userA), eq(placelistOwnedByB));
		}
	}

	/** Get placelist comments tests */
	@Nested
	@DisplayName("Get placelist comments tests")
	class GetPlacelistCommentsTests {

		@Test
		@DisplayName("Should return comments for existing placelist")
		void shouldReturnCommentsForExistingPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(commentRepo.getAll(eq(1), eq(1), eq(Placelist.class))).thenReturn(testComments);
			when(commentMapper.toDTOList(any())).thenReturn(testCommentDTOs);
			// When
			var result = placelistUC.getComments(1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canGet(eq(testUser), eq(testPlacelist));
			verify(commentRepo, times(1)).getAll(eq(1), eq(1), eq(Placelist.class));
			verify(commentMapper, times(1)).toDTOList(any());
		}

		@Test
		@DisplayName("Should throw exception when placelist does not exist")
		void shouldThrowExceptionWhenPlacelistDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Placelist not found")).when(placelistService).canGet(eq(testUser),
					eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.getComments(999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canGet(eq(testUser), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			doThrow(new RestrictedAccessException("User cannot access this placelist")).when(placelistService)
					.canGet(eq(testUser), eq(testPlacelist));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.getComments(1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canGet(eq(testUser), eq(testPlacelist));
		}
	}

	/** Add placelist comment tests */
	@Nested
	@DisplayName("Add placelist comment tests")
	class AddPlacelistCommentTests {

		@Test
		@DisplayName("Should add comment to existing placelist")
		void shouldAddCommentToValidPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(commentRepo.add(any(), any(), eq(Placelist.class))).thenReturn(null);
			when(commentMapper.toDTO(any())).thenReturn(testCommentDTO);
			// When
			var result = placelistUC.addComment(1, testCommentDTO, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canAddComment(eq(testUser), eq(testPlacelist), eq(false));
			verify(commentRepo, times(1)).add(any(), any(), eq(Placelist.class));
			verify(commentMapper, times(1)).toDTO(any());
		}

		@Test
		@DisplayName("Should throw exception when placelist does not exist")
		void shouldThrowExceptionWhenPlacelistDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Placelist not found")).when(placelistService)
					.canAddComment(eq(testUser), eq(null), eq(false));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.addComment(999, testCommentDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canAddComment(eq(testUser), eq(null), eq(false));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			doThrow(new RestrictedAccessException("User cannot comment on this placelist")).when(placelistService)
					.canAddComment(eq(testUser), eq(testPlacelist), eq(false));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.addComment(1, testCommentDTO, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canAddComment(eq(testUser), eq(testPlacelist), eq(false));
		}
	}

	/** Delete placelist comment tests */
	@Nested
	@DisplayName("Delete placelist comment tests")
	class DeletePlacelistCommentTests {

		@Test
		@DisplayName("Should delete comment from existing placelist")
		void shouldDeleteCommentFromValidPlacelist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(commentRepo.get(eq(1), eq(Placelist.class))).thenReturn(testComment);
			when(commentRepo.delete(eq(1), eq(Placelist.class))).thenReturn(testComment);
			when(commentMapper.toDTO(any())).thenReturn(testCommentDTO);
			// When
			var result = placelistUC.deleteComment(1, 1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(commentRepo, times(1)).get(eq(1), eq(Placelist.class));
			verify(placelistService, times(1)).canDeleteComment(eq(testUser), eq(testPlacelist), eq(testComment));
			verify(commentRepo, times(1)).delete(eq(1), eq(Placelist.class));
			verify(commentMapper, times(1)).toDTO(any());
		}

		@Test
		@DisplayName("Should throw exception when placelist does not exist")
		void shouldThrowExceptionWhenPlacelistDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Placelist not found")).when(placelistService)
					.canDeleteComment(eq(testUser), eq(null), eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.deleteComment(999, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canDeleteComment(eq(testUser), eq(null), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when comment does not exist")
		void shouldThrowExceptionWhenCommentDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(commentRepo.get(eq(999), eq(Placelist.class))).thenReturn(null);
			doThrow(new RestrictedAccessException("Comment not found")).when(placelistService)
					.canDeleteComment(eq(testUser), eq(testPlacelist), eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.deleteComment(1, 999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(commentRepo, times(1)).get(eq(999), eq(Placelist.class));
			verify(placelistService, times(1)).canDeleteComment(eq(testUser), eq(testPlacelist), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(commentRepo.get(eq(1), eq(Placelist.class))).thenReturn(null);
			doThrow(new RestrictedAccessException("User cannot delete this comment")).when(placelistService)
					.canDeleteComment(eq(testUser), eq(testPlacelist), eq(null));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.deleteComment(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(commentRepo, times(1)).get(eq(1), eq(Placelist.class));
			verify(placelistService, times(1)).canDeleteComment(eq(testUser), eq(testPlacelist), eq(null));
		}

		@Test
		@DisplayName("Should throw exception when comment is not associated with the placelist")
		void shouldThrowExceptionWhenCommentIsNotAssociatedWithThePlacelist()
				throws FunctionalException, TechnicalException {
			// Given
			PlacelistUser otherPlacelist = new PlacelistUser();
			otherPlacelist.setId(2);
			CommentPlacelist otherComment = new CommentPlacelist();
			otherComment.setId(1);
			otherComment.setOwner(testUser);
			otherComment.setPlacelist(otherPlacelist);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(commentRepo.get(eq(1), eq(Placelist.class))).thenReturn(otherComment);
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.deleteComment(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(commentRepo, times(1)).get(eq(1), eq(Placelist.class));
			verify(placelistService, times(1)).canDeleteComment(eq(testUser), eq(testPlacelist), eq(otherComment));
		}
	}

	/** Add place to placelist tests */
	@Nested
	@DisplayName("Add place to placelist tests")
	class AddPlaceToPlacelistTests {

		@Test
		@DisplayName("Should add place to existing placelist")
		void shouldAddPlaceToValidPlacelist() throws FunctionalException, TechnicalException {
			// Given
			PlaceDTO testPlaceDTO = new PlaceDTO();
			testPlaceDTO.setId(1);
			testPlaceDTO.setTitle("Test Place");
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(placelistRepo.countPlacesIn(eq(1))).thenReturn(0);
			when(placelistRepo.addPlace(eq(1), eq(1))).thenReturn(testPlace);
			when(placeMapper.toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(testPlaceDTO);
			// When
			var result = placelistUC.addPlace(1, 1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placelistRepo, times(1)).countPlacesIn(eq(1));
			verify(placelistService, times(1)).canAddPlace(eq(testUser), eq(testPlacelist), eq(testPlace), eq(0));
			verify(placelistRepo, times(1)).addPlace(eq(1), eq(1));
			verify(placeMapper, times(1)).toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when placelist does not exist")
		void shouldThrowExceptionWhenPlacelistDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE))).thenReturn(null);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			doThrow(new RestrictedAccessException("Placelist not found")).when(placelistService)
					.canAddPlace(eq(testUser), eq(null), eq(testPlace), eq(0));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.addPlace(999, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placelistService, times(1)).canAddPlace(eq(testUser), eq(null), eq(testPlace), eq(0));
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placeRepo.get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(null);
			doThrow(new RestrictedAccessException("Place not found")).when(placelistService).canAddPlace(eq(testUser),
					eq(testPlacelist), eq(null), eq(0));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.addPlace(1, 999, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placeRepo, times(1)).get(eq(999), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placelistService, times(1)).canAddPlace(eq(testUser), eq(testPlacelist), eq(null), eq(0));
		}

		@Test
		@DisplayName("Should throw exception when placelist has reached place limit")
		void shouldThrowExceptionWhenPlacelistHasReachedLimit() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(placelistRepo.countPlacesIn(eq(1))).thenReturn(10);
			doThrow(new RestrictedAccessException("Placelist has reached place limit")).when(placelistService)
					.canAddPlace(eq(testUser), eq(testPlacelist), eq(testPlace), eq(10));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.addPlace(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placelistRepo, times(1)).countPlacesIn(eq(1));
			verify(placelistService, times(1)).canAddPlace(eq(testUser), eq(testPlacelist), eq(testPlace), eq(10));
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placeRepo.get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE))).thenReturn(testPlace);
			when(placelistRepo.countPlacesIn(eq(1))).thenReturn(0);
			doThrow(new RestrictedAccessException("User cannot add place to this placelist")).when(placelistService)
					.canAddPlace(eq(testUser), eq(testPlacelist), eq(testPlace), eq(0));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.addPlace(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placeRepo, times(1)).get(eq(1), eq(CommentMode.NONE), eq(PhotoMode.NONE));
			verify(placelistRepo, times(1)).countPlacesIn(eq(1));
			verify(placelistService, times(1)).canAddPlace(eq(testUser), eq(testPlacelist), eq(testPlace), eq(0));
		}
	}

	/** Remove place from placelist tests */
	@Nested
	@DisplayName("Remove place from placelist tests")
	class RemovePlaceFromPlacelistTests {

		@Test
		@DisplayName("Should remove place from existing placelist")
		void shouldRemovePlaceFromValidPlacelist() throws FunctionalException, TechnicalException {
			// Given
			PlaceDTO testPlaceDTO = new PlaceDTO();
			testPlaceDTO.setId(1);
			testPlaceDTO.setTitle("Test Place");

			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placelistRepo.removePlace(eq(1), eq(1))).thenReturn(testPlace);
			when(placeMapper.toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE)))
					.thenReturn(testPlaceDTO);
			// When
			var result = placelistUC.removePlace(1, 1, 1);
			// Then
			assertNotNull(result);
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canRemovePlace(eq(testUser), eq(testPlacelist));
			verify(placelistRepo, times(1)).removePlace(eq(1), eq(1));
			verify(placeMapper, times(1)).toDTO(any(Place.class), eq(CommentMode.NONE), eq(PhotoMode.NONE));
		}

		@Test
		@DisplayName("Should throw exception when placelist does not exist")
		void shouldThrowExceptionWhenPlacelistDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenThrow(new NotExistPlacelistException("Placelist does not exist"));
			// When/Then
			NotExistPlacelistException nepex = assertThrows(NotExistPlacelistException.class,
					() -> placelistUC.removePlace(999, 1, 1));
			assertNotNull(nepex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(999), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, never()).canRemovePlace(any(), any());
			verify(placelistRepo, never()).removePlace(any(), any());
		}

		@Test
		@DisplayName("Should throw exception when user lacks permission")
		void shouldThrowExceptionWhenUserLacksPermission() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			doThrow(new RestrictedAccessException("User lacks permission")).when(placelistService)
					.canRemovePlace(eq(testUser), eq(testPlacelist));
			// When/Then
			RestrictedAccessException raex = assertThrows(RestrictedAccessException.class,
					() -> placelistUC.removePlace(1, 1, 1));
			assertNotNull(raex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canRemovePlace(eq(testUser), eq(testPlacelist));
			verify(placelistRepo, never()).removePlace(any(), any());
		}

		@Test
		@DisplayName("Should throw exception when place does not exist")
		void shouldThrowExceptionWhenPlaceDoesNotExist() throws FunctionalException, TechnicalException {
			// Given
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placelistRepo.removePlace(eq(1), eq(999)))
					.thenThrow(new NotExistPlaceException("Place does not exist"));
			// When/Then
			NotExistPlaceException nepex = assertThrows(NotExistPlaceException.class,
					() -> placelistUC.removePlace(1, 999, 1));
			assertNotNull(nepex.getMessage());
			verify(userRepo, times(1)).getById(eq(1));
			verify(placelistRepo, times(1)).get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE));
			verify(placelistService, times(1)).canRemovePlace(eq(testUser), eq(testPlacelist));
			verify(placelistRepo, times(1)).removePlace(eq(1), eq(999));
		}
	}

	/** Count owner placelists tests */
	@Nested
	@DisplayName("Count owner placelists tests")
	class CountOwnerPlacelistsTests {

		@Test
		@DisplayName("Should return count DTO for owner placelists")
		void shouldReturnCountDtoForOwnerPlacelists() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(2);
			when(placelistRepo.count(eq(1))).thenReturn(2);
			when(countMapper.toDTO(eq(2))).thenReturn(expected);
			CountDTO result = placelistUC.countOwnerPlacelists(1);
			assertNotNull(result);
			assertEquals(2, result.getCount());
			verify(placelistRepo, times(1)).count(eq(1));
			verify(countMapper, times(1)).toDTO(eq(2));
		}
	}

	/** Count owner comment on placelist tests */
	@Nested
	@DisplayName("Count owner comment on placelist tests")
	class CountOwnerCommentOnPlacelistTests {

		@Test
		@DisplayName("Should return count of placelist comments by requesting user")
		void shouldReturnCountOfCommentsByRequestingUser() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(1);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(commentRepo.getAll(eq(1), eq(1), eq(Placelist.class))).thenReturn(testComments);
			when(countMapper.toDTO(eq(1))).thenReturn(expected);
			CountDTO result = placelistUC.countOwnerComment(1, 1);
			assertNotNull(result);
			assertEquals(1, result.getCount());
			verify(placelistService, times(1)).canGet(eq(testUser), eq(testPlacelist));
			verify(countMapper, times(1)).toDTO(eq(1));
		}

		@Test
		@DisplayName("Should throw when user cannot access placelist for comment count")
		void shouldThrowWhenUserCannotAccessPlacelist() throws FunctionalException, TechnicalException {
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			doThrow(new RestrictedAccessException("denied")).when(placelistService).canGet(eq(testUser),
					eq(testPlacelist));
			assertThrows(RestrictedAccessException.class, () -> placelistUC.countOwnerComment(1, 1));
			verify(commentRepo, never()).getAll(any(), any(), any());
		}
	}

	/** Count places in placelist tests */
	@Nested
	@DisplayName("Count places in placelist tests")
	class CountPlacesInPlacelistTests {

		@Test
		@DisplayName("Should return number of places in placelist")
		void shouldReturnNumberOfPlacesInPlacelist() throws FunctionalException, TechnicalException {
			CountDTO expected = new CountDTO();
			expected.setCount(5);
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			when(placelistRepo.countPlacesIn(eq(1))).thenReturn(5);
			when(countMapper.toDTO(eq(5))).thenReturn(expected);
			CountDTO result = placelistUC.countPlacesInPlacelist(1, 1);
			assertNotNull(result);
			assertEquals(5, result.getCount());
			verify(placelistRepo, times(1)).countPlacesIn(eq(1));
			verify(countMapper, times(1)).toDTO(eq(5));
		}

		@Test
		@DisplayName("Should throw when user cannot read placelist for place count")
		void shouldThrowWhenUserCannotReadPlacelist() throws FunctionalException, TechnicalException {
			when(userRepo.getById(eq(1))).thenReturn(testUser);
			when(placelistRepo.get(eq(1), eq(PlacelistMode.WITHOUT_PLACES), eq(CommentMode.NONE)))
					.thenReturn(testPlacelist);
			doThrow(new RestrictedAccessException("denied")).when(placelistService).canGet(eq(testUser),
					eq(testPlacelist));
			assertThrows(RestrictedAccessException.class, () -> placelistUC.countPlacesInPlacelist(1, 1));
			verify(placelistRepo, never()).countPlacesIn(any());
		}
	}
}
