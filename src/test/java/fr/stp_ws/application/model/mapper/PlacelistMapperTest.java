package fr.stp_ws.application.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import fr.stp_ws.application.model.mapper.impl.PlacelistMapper;
import fr.stp_ws.application.service.inter.IPlacelistEnrichmentService;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.PlacelistTourist;
import fr.stp_ws.data.model.PlacelistUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;

/**
 * Placelist mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Placelist mapper tests")
@ExtendWith(MockitoExtension.class)
class PlacelistMapperTest {

	private static final Integer PLACELIST_ID = 1;
	private static final String USER_NAME = "Test User";
	private static final EntityType TYPE = EntityType.EAT;
	private static final String TITLE = "Test Placelist";
	private static final Float RATING = 4.5f;
	private static final String DESCRIPTION = "Test description";
	private static final Boolean VISIBILITY = true;
	private static final Timestamp CREATION_DATE = new Timestamp(System.currentTimeMillis());
	private static final Timestamp LAST_UPDATE = new Timestamp(System.currentTimeMillis());
	@InjectMocks
	private PlacelistMapper placelistMapper;
	@Mock
	private IPlacelistEnrichmentService enrichmentService;
	@Mock
	private User mockUser;
	@Mock
	private Placelist mockPlacelist;
	private User testUser;
	private PlacelistUser testPlacelist;
	private PlacelistDTO testPlacelistDTO;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		testUser = new User();
		testUser.setName(USER_NAME);
		testUser.setTourist(false);
		// Placelist initialization
		testPlacelist = new PlacelistUser();
		testPlacelist.setId(PLACELIST_ID);
		testPlacelist.setOwner(testUser);
		testPlacelist.setType(TYPE);
		testPlacelist.setTitle(TITLE);
		testPlacelist.setRating(RATING);
		testPlacelist.setDescription(DESCRIPTION);
		testPlacelist.setVisibility(VISIBILITY);
		testPlacelist.setCreationDate(CREATION_DATE);
		testPlacelist.setLastUpdate(LAST_UPDATE);
		testPlacelist.setPlaces(new ArrayList<>());
		testPlacelist.setComments(new ArrayList<>());
		// Placelist DTO initialization
		testPlacelistDTO = new PlacelistDTO();
		testPlacelistDTO.setId(PLACELIST_ID);
		testPlacelistDTO.setType(TYPE);
		testPlacelistDTO.setTitle(TITLE);
		testPlacelistDTO.setRating(RATING);
		testPlacelistDTO.setDescription(DESCRIPTION);
		testPlacelistDTO.setVisibility(VISIBILITY);
		testPlacelistDTO.setCreationDate(CREATION_DATE);
		testPlacelistDTO.setLastUpdate(LAST_UPDATE);
		testPlacelistDTO.setOwner(USER_NAME);
		testPlacelistDTO.setCategory(EntityCategory.USER);
		testPlacelistDTO.setPlaces(new ArrayList<>());
		testPlacelistDTO.setComments(new ArrayList<>());
	}

	/** Map placelist to full DTO tests */
	@Nested
	@DisplayName("Map placelist to full DTO tests")
	class ToDTOTests {

		@Test
		@DisplayName("Should convert a valid placelist with enrichment")
		void shouldConvertValidPlacelistWithEnrichment() {
			// Given
			doNothing().when(enrichmentService).enrichWithComments(any(Placelist.class), any(PlacelistDTO.class),
					eq(CommentMode.MAX_3));
			doNothing().when(enrichmentService).enrichWithPlaces(any(Placelist.class), any(PlacelistDTO.class),
					eq(PlacelistMode.WITH_PLACES));
			// When
			PlacelistDTO result = placelistMapper.toDTO(testPlacelist, PlacelistMode.WITH_PLACES, CommentMode.MAX_3);
			// Then
			assertNotNull(result);
			assertEquals(PLACELIST_ID, result.getId());
			assertEquals(USER_NAME, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TITLE, result.getTitle());
			assertEquals(RATING, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertEquals(CREATION_DATE, result.getCreationDate());
			assertEquals(LAST_UPDATE, result.getLastUpdate());
			assertEquals(EntityCategory.USER, result.getCategory());
			verify(enrichmentService).enrichWithComments(eq(testPlacelist), any(PlacelistDTO.class),
					eq(CommentMode.MAX_3));
			verify(enrichmentService).enrichWithPlaces(eq(testPlacelist), any(PlacelistDTO.class),
					eq(PlacelistMode.WITH_PLACES));
		}

		@Test
		@DisplayName("Should return null for a null placelist")
		void shouldReturnNullForNullPlacelist() {
			// When
			PlacelistDTO result = placelistMapper.toDTO(null, PlacelistMode.WITH_PLACES, CommentMode.MAX_3);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should handle LazyInitializationException for user")
		void shouldHandleLazyInitializationExceptionForUser() {
			// Given
			when(mockPlacelist.getId()).thenReturn(PLACELIST_ID);
			when(mockPlacelist.getOwner()).thenThrow(LazyInitializationException.class);
			// When
			PlacelistDTO result = placelistMapper.toDTO(mockPlacelist, PlacelistMode.WITH_PLACES, CommentMode.MAX_3);
			// Then
			assertNotNull(result);
			assertEquals(PLACELIST_ID, result.getId());
			assertEquals("User unknown", result.getOwner());
			assertEquals(EntityCategory.USER, result.getCategory());
		}
	}

	/** Map placelist to basic DTO tests */
	@Nested
	@DisplayName("Map placelist to basic DTO tests")
	class ToBasicDTOTests {

		@Test
		@DisplayName("Should convert a valid placelist")
		void shouldConvertValidPlacelist() {
			// When
			PlacelistDTO result = placelistMapper.toBasicDTO(testPlacelist);
			// Then
			assertNotNull(result);
			assertEquals(PLACELIST_ID, result.getId());
			assertEquals(USER_NAME, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TITLE, result.getTitle());
			assertEquals(RATING, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertEquals(CREATION_DATE, result.getCreationDate());
			assertEquals(LAST_UPDATE, result.getLastUpdate());
			assertEquals(EntityCategory.USER, result.getCategory());
			assertTrue(result.getPlaces().isEmpty());
			assertTrue(result.getComments().isEmpty());
		}

		@Test
		@DisplayName("Should return null for a null placelist")
		void shouldReturnNullForNullPlacelist() {
			// When
			PlacelistDTO result = placelistMapper.toBasicDTO(null);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should handle LazyInitializationException for user")
		void shouldHandleLazyInitializationExceptionForUser() {
			// Given
			when(mockPlacelist.getId()).thenReturn(PLACELIST_ID);
			when(mockPlacelist.getOwner()).thenThrow(LazyInitializationException.class);
			// When
			PlacelistDTO result = placelistMapper.toBasicDTO(mockPlacelist);
			// Then
			assertNotNull(result);
			assertEquals(PLACELIST_ID, result.getId());
			assertEquals("User unknown", result.getOwner());
			assertEquals(EntityCategory.USER, result.getCategory());
		}
	}

	/** Map placelist list to full DTO list tests */
	@Nested
	@DisplayName("Map placelist list to full DTO list tests")
	class ToDTOListTests {

		@Test
		@DisplayName("Should convert a list of valid placelists")
		void shouldConvertValidPlacelistList() {
			// Given
			List<Placelist> placelists = List.of(testPlacelist);
			doNothing().when(enrichmentService).enrichWithComments(any(Placelist.class), any(PlacelistDTO.class),
					eq(CommentMode.MAX_3));
			doNothing().when(enrichmentService).enrichWithPlaces(any(Placelist.class), any(PlacelistDTO.class),
					eq(PlacelistMode.WITH_PLACES));
			// When
			List<PlacelistDTO> result = placelistMapper.toDTOList(placelists, PlacelistMode.WITH_PLACES,
					CommentMode.MAX_3);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			PlacelistDTO dto = result.get(0);
			assertEquals(PLACELIST_ID, dto.getId());
			assertEquals(USER_NAME, dto.getOwner());
			assertEquals(TYPE, dto.getType());
			verify(enrichmentService).enrichWithComments(eq(testPlacelist), any(PlacelistDTO.class),
					eq(CommentMode.MAX_3));
			verify(enrichmentService).enrichWithPlaces(eq(testPlacelist), any(PlacelistDTO.class),
					eq(PlacelistMode.WITH_PLACES));
		}

		@Test
		@DisplayName("Should return an empty list for a null list")
		void shouldReturnEmptyListForNullList() {
			// When
			List<PlacelistDTO> result = placelistMapper.toDTOList(null, PlacelistMode.WITH_PLACES, CommentMode.MAX_3);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for an empty list")
		void shouldReturnEmptyListForEmptyList() {
			// When
			List<PlacelistDTO> result = placelistMapper.toDTOList(new ArrayList<>(), PlacelistMode.WITH_PLACES,
					CommentMode.MAX_3);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}
	}

	/** Map placelist list to basic DTO list tests */
	@Nested
	@DisplayName("Map placelist list to basic DTO list tests")
	class ToBasicDTOListTests {

		@Test
		@DisplayName("Should convert a list of valid placelists")
		void shouldConvertValidPlacelistList() {
			// Given
			List<Placelist> placelists = List.of(testPlacelist);
			// When
			List<PlacelistDTO> result = placelistMapper.toBasicDTOList(placelists);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			PlacelistDTO dto = result.get(0);
			assertEquals(PLACELIST_ID, dto.getId());
			assertEquals(USER_NAME, dto.getOwner());
			assertEquals(TYPE, dto.getType());
			assertTrue(dto.getPlaces().isEmpty());
			assertTrue(dto.getComments().isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for a null list")
		void shouldReturnEmptyListForNullList() {
			// When
			List<PlacelistDTO> result = placelistMapper.toBasicDTOList(null);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for an empty list")
		void shouldReturnEmptyListForEmptyList() {
			// When
			List<PlacelistDTO> result = placelistMapper.toBasicDTOList(new ArrayList<>());
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}
	}

	/** Map DTO to placelist entity tests */
	@Nested
	@DisplayName("Map DTO to placelist entity tests")
	class ToEntityTests {

		@Test
		@DisplayName("Should convert to PlacelistUser for a standard user")
		void shouldConvertToPlacelistUserForNormalUser() {
			// Given
			when(mockUser.getTourist()).thenReturn(false);
			// When
			Placelist result = placelistMapper.toEntity(testPlacelistDTO, mockUser);
			// Then
			assertNotNull(result);
			assertTrue(result instanceof PlacelistUser);
			assertEquals(PLACELIST_ID, result.getId());
			assertEquals(mockUser, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TITLE, result.getTitle());
			assertEquals(0.0f, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertNotNull(result.getCreationDate());
			assertNotNull(result.getLastUpdate());
			assertTrue(result.getPlaces().isEmpty());
			assertTrue(result.getComments().isEmpty());
		}

		@Test
		@DisplayName("Should convert to PlacelistTourist for a tourist office user")
		void shouldConvertToPlacelistTouristForTouristUser() {
			// Given
			when(mockUser.getTourist()).thenReturn(true);
			// When
			Placelist result = placelistMapper.toEntity(testPlacelistDTO, mockUser);
			// Then
			assertNotNull(result);
			assertTrue(result instanceof PlacelistTourist);
			assertEquals(PLACELIST_ID, result.getId());
			assertEquals(mockUser, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TITLE, result.getTitle());
			assertEquals(0.0f, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertNotNull(result.getCreationDate());
			assertNotNull(result.getLastUpdate());
			assertTrue(result.getPlaces().isEmpty());
			assertTrue(result.getComments().isEmpty());
		}

		@Test
		@DisplayName("Should return null for a null DTO")
		void shouldReturnNullForNullDTO() {
			// When
			Placelist result = placelistMapper.toEntity(null, mockUser);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should preserve creation date when converting")
		void shouldKeepCreationDateWhenConverting() {
			// When
			Placelist result = placelistMapper.toEntity(testPlacelistDTO, mockUser);
			// Then
			assertNotNull(result);
			assertEquals(CREATION_DATE, result.getCreationDate());
		}

		@Test
		@DisplayName("Should update last update timestamp")
		void shouldUpdateLastUpdateDate() {
			// When
			Placelist result = placelistMapper.toEntity(testPlacelistDTO, mockUser);
			// Then
			assertNotNull(result);
			assertTrue(result.getLastUpdate().after(LAST_UPDATE));
		}
	}
}
