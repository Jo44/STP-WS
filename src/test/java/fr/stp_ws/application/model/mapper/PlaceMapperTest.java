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

import fr.stp_ws.application.model.mapper.impl.PlaceMapper;
import fr.stp_ws.application.service.inter.IPlaceEnrichmentService;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceTourist;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;

/**
 * Place mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Place mapper tests")
@ExtendWith(MockitoExtension.class)
class PlaceMapperTest {

	private static final Integer PLACE_ID = 1;
	private static final String USER_NAME = "Test User";
	private static final EntityType TYPE = EntityType.EAT;
	private static final String TIMES = "12:00-14:00";
	private static final Double LATITUDE = 48.8566;
	private static final Double LONGITUDE = 2.3522;
	private static final String TITLE = "Test Place";
	private static final Float RATING = 4.5f;
	private static final String DESCRIPTION = "Test description";
	private static final Boolean VISIBILITY = true;
	private static final Timestamp CREATION_DATE = new Timestamp(System.currentTimeMillis());
	private static final Timestamp LAST_UPDATE = new Timestamp(System.currentTimeMillis());
	@InjectMocks
	private PlaceMapper placeMapper;
	@Mock
	private IPlaceEnrichmentService enrichmentService;
	@Mock
	private User mockUser;
	@Mock
	private PlaceUser mockPlace;
	private User testUser;
	private PlaceUser testPlace;
	private PlaceDTO testPlaceDTO;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		testUser = new User();
		testUser.setName(USER_NAME);
		testUser.setTourist(false);
		// Place initialization
		testPlace = new PlaceUser();
		testPlace.setId(PLACE_ID);
		testPlace.setOwner(testUser);
		testPlace.setType(TYPE);
		testPlace.setTimes(TIMES);
		testPlace.setLatitude(LATITUDE);
		testPlace.setLongitude(LONGITUDE);
		testPlace.setTitle(TITLE);
		testPlace.setRating(RATING);
		testPlace.setDescription(DESCRIPTION);
		testPlace.setVisibility(VISIBILITY);
		testPlace.setCreationDate(CREATION_DATE);
		testPlace.setLastUpdate(LAST_UPDATE);
		testPlace.setComments(new ArrayList<>());
		testPlace.setPhotos(new ArrayList<>());
		// Place DTO initialization
		testPlaceDTO = new PlaceDTO();
		testPlaceDTO.setId(PLACE_ID);
		testPlaceDTO.setType(TYPE);
		testPlaceDTO.setTimes(TIMES);
		testPlaceDTO.setLatitude(LATITUDE);
		testPlaceDTO.setLongitude(LONGITUDE);
		testPlaceDTO.setTitle(TITLE);
		testPlaceDTO.setRating(RATING);
		testPlaceDTO.setDescription(DESCRIPTION);
		testPlaceDTO.setVisibility(VISIBILITY);
		testPlaceDTO.setCreationDate(CREATION_DATE);
		testPlaceDTO.setLastUpdate(LAST_UPDATE);
		testPlaceDTO.setOwner(USER_NAME);
		testPlaceDTO.setCategory(EntityCategory.USER);
		testPlaceDTO.setComments(new ArrayList<>());
		testPlaceDTO.setPhotos(new ArrayList<>());
	}

	/** Map place to full DTO tests */
	@Nested
	@DisplayName("Map place to full DTO tests")
	class ToDTOTests {

		@Test
		@DisplayName("Should convert a valid place with enrichment")
		void shouldConvertValidPlaceWithEnrichment() {
			// Given
			doNothing().when(enrichmentService).enrichWithComments(any(Place.class), any(PlaceDTO.class),
					eq(CommentMode.MAX_3));
			doNothing().when(enrichmentService).enrichWithPhotos(any(Place.class), any(PlaceDTO.class),
					eq(PhotoMode.MAX_3));
			// When
			PlaceDTO result = placeMapper.toDTO(testPlace, CommentMode.MAX_3, PhotoMode.MAX_3);
			// Then
			assertNotNull(result);
			assertEquals(PLACE_ID, result.getId());
			assertEquals(USER_NAME, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TIMES, result.getTimes());
			assertEquals(LATITUDE, result.getLatitude());
			assertEquals(LONGITUDE, result.getLongitude());
			assertEquals(TITLE, result.getTitle());
			assertEquals(RATING, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertEquals(CREATION_DATE, result.getCreationDate());
			assertEquals(LAST_UPDATE, result.getLastUpdate());
			assertEquals(EntityCategory.USER, result.getCategory());
			verify(enrichmentService).enrichWithComments(eq(testPlace), any(PlaceDTO.class), eq(CommentMode.MAX_3));
			verify(enrichmentService).enrichWithPhotos(eq(testPlace), any(PlaceDTO.class), eq(PhotoMode.MAX_3));
		}

		@Test
		@DisplayName("Should return null for a null place")
		void shouldReturnNullForNullPlace() {
			// When
			PlaceDTO result = placeMapper.toDTO(null, CommentMode.MAX_3, PhotoMode.MAX_3);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should handle LazyInitializationException for user")
		void shouldHandleLazyInitializationExceptionForUser() {
			// Given
			when(mockPlace.getId()).thenReturn(PLACE_ID);
			when(mockPlace.getOwner()).thenThrow(LazyInitializationException.class);
			// When
			PlaceDTO result = placeMapper.toDTO(mockPlace, CommentMode.MAX_3, PhotoMode.MAX_3);
			// Then
			assertNotNull(result);
			assertEquals(PLACE_ID, result.getId());
			assertEquals("User unknown", result.getOwner());
			assertEquals(EntityCategory.USER, result.getCategory());
		}
	}

	/** Map place to basic DTO tests */
	@Nested
	@DisplayName("Map place to basic DTO tests")
	class ToBasicDTOTests {

		@Test
		@DisplayName("Should convert a valid place")
		void shouldConvertValidPlace() {
			// When
			PlaceDTO result = placeMapper.toBasicDTO(testPlace);
			// Then
			assertNotNull(result);
			assertEquals(PLACE_ID, result.getId());
			assertEquals(USER_NAME, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TIMES, result.getTimes());
			assertEquals(LATITUDE, result.getLatitude());
			assertEquals(LONGITUDE, result.getLongitude());
			assertEquals(TITLE, result.getTitle());
			assertEquals(RATING, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertEquals(CREATION_DATE, result.getCreationDate());
			assertEquals(LAST_UPDATE, result.getLastUpdate());
			assertEquals(EntityCategory.USER, result.getCategory());
			assertTrue(result.getComments().isEmpty());
			assertTrue(result.getPhotos().isEmpty());
		}

		@Test
		@DisplayName("Should return null for a null place")
		void shouldReturnNullForNullPlace() {
			// When
			PlaceDTO result = placeMapper.toBasicDTO(null);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should handle LazyInitializationException for user")
		void shouldHandleLazyInitializationExceptionForUser() {
			// Given
			when(mockPlace.getId()).thenReturn(PLACE_ID);
			when(mockPlace.getOwner()).thenThrow(LazyInitializationException.class);
			// When
			PlaceDTO result = placeMapper.toBasicDTO(mockPlace);
			// Then
			assertNotNull(result);
			assertEquals(PLACE_ID, result.getId());
			assertEquals("User unknown", result.getOwner());
			assertEquals(EntityCategory.USER, result.getCategory());
		}
	}

	/** Map place list to full DTO list tests */
	@Nested
	@DisplayName("Map place list to full DTO list tests")
	class ToDTOListTests {

		@Test
		@DisplayName("Should convert a list of valid places")
		void shouldConvertValidPlacelist() {
			// Given
			List<Place> places = List.of(testPlace);
			doNothing().when(enrichmentService).enrichWithComments(any(Place.class), any(PlaceDTO.class),
					eq(CommentMode.MAX_3));
			doNothing().when(enrichmentService).enrichWithPhotos(any(Place.class), any(PlaceDTO.class),
					eq(PhotoMode.MAX_3));
			// When
			List<PlaceDTO> result = placeMapper.toDTOList(places, CommentMode.MAX_3, PhotoMode.MAX_3);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			PlaceDTO dto = result.get(0);
			assertEquals(PLACE_ID, dto.getId());
			assertEquals(USER_NAME, dto.getOwner());
			assertEquals(TYPE, dto.getType());
			verify(enrichmentService).enrichWithComments(eq(testPlace), any(PlaceDTO.class), eq(CommentMode.MAX_3));
			verify(enrichmentService).enrichWithPhotos(eq(testPlace), any(PlaceDTO.class), eq(PhotoMode.MAX_3));
		}

		@Test
		@DisplayName("Should return an empty list for a null list")
		void shouldReturnEmptyListForNullList() {
			// When
			List<PlaceDTO> result = placeMapper.toDTOList(null, CommentMode.MAX_3, PhotoMode.MAX_3);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for an empty list")
		void shouldReturnEmptyListForEmptyList() {
			// When
			List<PlaceDTO> result = placeMapper.toDTOList(new ArrayList<>(), CommentMode.MAX_3, PhotoMode.MAX_3);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}
	}

	/** Map place list to basic DTO list tests */
	@Nested
	@DisplayName("Map place list to basic DTO list tests")
	class ToBasicDTOListTests {

		@Test
		@DisplayName("Should convert a list of valid places")
		void shouldConvertValidPlacelist() {
			// Given
			List<Place> places = List.of(testPlace);
			// When
			List<PlaceDTO> result = placeMapper.toBasicDTOList(places);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			PlaceDTO dto = result.get(0);
			assertEquals(PLACE_ID, dto.getId());
			assertEquals(USER_NAME, dto.getOwner());
			assertEquals(TYPE, dto.getType());
			assertTrue(dto.getComments().isEmpty());
			assertTrue(dto.getPhotos().isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for a null list")
		void shouldReturnEmptyListForNullList() {
			// When
			List<PlaceDTO> result = placeMapper.toBasicDTOList(null);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for an empty list")
		void shouldReturnEmptyListForEmptyList() {
			// When
			List<PlaceDTO> result = placeMapper.toBasicDTOList(new ArrayList<>());
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}
	}

	/** Map DTO to place entity tests */
	@Nested
	@DisplayName("Map DTO to place entity tests")
	class ToEntityTests {

		@Test
		@DisplayName("Should convert to PlaceUser for a standard user")
		void shouldConvertToPlaceUserForNormalUser() {
			// Given
			when(mockUser.getTourist()).thenReturn(false);
			// When
			Place result = placeMapper.toEntity(testPlaceDTO, mockUser);
			// Then
			assertNotNull(result);
			assertTrue(result instanceof PlaceUser);
			assertEquals(PLACE_ID, result.getId());
			assertEquals(mockUser, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TIMES, result.getTimes());
			assertEquals(LATITUDE, result.getLatitude());
			assertEquals(LONGITUDE, result.getLongitude());
			assertEquals(TITLE, result.getTitle());
			assertEquals(0.0f, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertNotNull(result.getCreationDate());
			assertNotNull(result.getLastUpdate());
		}

		@Test
		@DisplayName("Should convert to PlaceTourist for a tourist office user")
		void shouldConvertToPlaceTouristForTouristUser() {
			// Given
			when(mockUser.getTourist()).thenReturn(true);
			// When
			Place result = placeMapper.toEntity(testPlaceDTO, mockUser);
			// Then
			assertNotNull(result);
			assertTrue(result instanceof PlaceTourist);
			assertEquals(PLACE_ID, result.getId());
			assertEquals(mockUser, result.getOwner());
			assertEquals(TYPE, result.getType());
			assertEquals(TIMES, result.getTimes());
			assertEquals(LATITUDE, result.getLatitude());
			assertEquals(LONGITUDE, result.getLongitude());
			assertEquals(TITLE, result.getTitle());
			assertEquals(0.0f, result.getRating());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(VISIBILITY, result.getVisibility());
			assertNotNull(result.getCreationDate());
			assertNotNull(result.getLastUpdate());
		}

		@Test
		@DisplayName("Should return null for a null DTO")
		void shouldReturnNullForNullDTO() {
			// When
			Place result = placeMapper.toEntity(null, mockUser);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should preserve creation date when converting")
		void shouldKeepCreationDateWhenConverting() {
			// When
			Place result = placeMapper.toEntity(testPlaceDTO, mockUser);
			// Then
			assertNotNull(result);
			assertEquals(CREATION_DATE, result.getCreationDate());
		}

		@Test
		@DisplayName("Should update last update timestamp")
		void shouldUpdateLastUpdateDate() {
			// When
			Place result = placeMapper.toEntity(testPlaceDTO, mockUser);
			// Then
			assertNotNull(result);
			assertTrue(result.getLastUpdate().after(LAST_UPDATE));
		}
	}
}
