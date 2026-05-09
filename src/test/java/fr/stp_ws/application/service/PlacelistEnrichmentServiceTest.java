package fr.stp_ws.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.service.impl.PlacelistEnrichmentService;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;

/**
 * Placelist enrichment service tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Placelist enrichment service tests")
@ExtendWith(MockitoExtension.class)
class PlacelistEnrichmentServiceTest {

	private static final Integer COMMENT_ID = 1;
	private static final Integer PLACE_ID = 1;
	private static final String USER_NAME = "Test User";
	private static final Float RATING = 4.5f;
	private static final String COMMENT_TEXT = "Test comment";
	private static final String PLACE_TITLE = "Test Place";
	private static final Timestamp CREATION_DATE = new Timestamp(System.currentTimeMillis());
	@InjectMocks
	private PlacelistEnrichmentService placelistEnrichmentService;
	@Mock
	private ICommentMapper commentMapper;
	@Mock
	private IPlaceMapper placeMapper;
	@Mock
	private Placelist mockPlacelist;
	private PlacelistDTO testPlacelistDTO;
	private List<CommentPlacelist> testComments;
	private List<Place> testPlaces;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		User testUser = new User();
		testUser.setName(USER_NAME);
		// Comments initialization
		testComments = new ArrayList<>();
		CommentPlacelist comment = new CommentPlacelist();
		comment.setId(COMMENT_ID);
		comment.setOwner(testUser);
		comment.setRating(RATING);
		comment.setMessage(COMMENT_TEXT);
		comment.setCreationDate(CREATION_DATE);
		testComments.add(comment);
		// Places initialization
		testPlaces = new ArrayList<>();
		PlaceUser place = new PlaceUser();
		testPlaces.add(place);
		// Placelist DTO initialization
		testPlacelistDTO = new PlacelistDTO();
	}

	/** Enrich placelist with comments tests */
	@Nested
	@DisplayName("Enrich placelist with comments tests")
	class EnrichWithCommentsTests {

		@Test
		@DisplayName("Should enrich with comments in MAX_3 mode")
		void shouldEnrichWithCommentsInMax3Mode() {
			// Given
			when(mockPlacelist.getComments()).thenReturn(testComments);
			CommentDTO commentDTO = new CommentDTO();
			commentDTO.setId(COMMENT_ID);
			commentDTO.setOwner(USER_NAME);
			commentDTO.setRating(RATING);
			commentDTO.setMessage(COMMENT_TEXT);
			commentDTO.setCreationDate(CREATION_DATE);
			when(commentMapper.toDTO(testComments.get(0))).thenReturn(commentDTO);
			// When
			placelistEnrichmentService.enrichWithComments(mockPlacelist, testPlacelistDTO, CommentMode.MAX_3);
			// Then
			assertNotNull(testPlacelistDTO.getComments());
			assertEquals(1, testPlacelistDTO.getComments().size());
			CommentDTO resultComment = testPlacelistDTO.getComments().get(0);
			assertEquals(COMMENT_ID, resultComment.getId());
			assertEquals(USER_NAME, resultComment.getOwner());
			assertEquals(RATING, resultComment.getRating());
			assertEquals(COMMENT_TEXT, resultComment.getMessage());
			assertEquals(CREATION_DATE, resultComment.getCreationDate());
		}

		@Test
		@DisplayName("Should handle LazyInitializationException")
		void shouldHandleLazyInitializationException() {
			// Given
			when(mockPlacelist.getComments()).thenThrow(LazyInitializationException.class);
			// When
			placelistEnrichmentService.enrichWithComments(mockPlacelist, testPlacelistDTO, CommentMode.MAX_3);
			// Then
			assertNotNull(testPlacelistDTO.getComments());
			assertTrue(testPlacelistDTO.getComments().isEmpty());
		}

		@Test
		@DisplayName("Should not enrich in NONE mode")
		void shouldNotEnrichInNoneMode() {
			// When
			placelistEnrichmentService.enrichWithComments(mockPlacelist, testPlacelistDTO, CommentMode.NONE);
			// Then
			assertNotNull(testPlacelistDTO.getComments());
			assertTrue(testPlacelistDTO.getComments().isEmpty());
		}
	}

	/** Enrich placelist with places tests */
	@Nested
	@DisplayName("Enrich placelist with places tests")
	class EnrichWithPlacesTests {

		@Test
		@DisplayName("Should enrich with places in WITH_PLACES mode")
		void shouldEnrichWithPlacesInWithPlacesMode() {
			// Given
			when(mockPlacelist.getPlaces()).thenReturn(testPlaces);
			List<PlaceDTO> placeDTOs = new ArrayList<>();
			PlaceDTO placeDTO = new PlaceDTO();
			placeDTO.setId(PLACE_ID);
			placeDTO.setTitle(PLACE_TITLE);
			placeDTOs.add(placeDTO);
			when(placeMapper.toDTOList(testPlaces, CommentMode.NONE, PhotoMode.NONE)).thenReturn(placeDTOs);
			// When
			placelistEnrichmentService.enrichWithPlaces(mockPlacelist, testPlacelistDTO, PlacelistMode.WITH_PLACES);
			// Then
			assertNotNull(testPlacelistDTO.getPlaces());
			assertEquals(1, testPlacelistDTO.getPlaces().size());
			PlaceDTO resultPlace = testPlacelistDTO.getPlaces().get(0);
			assertEquals(PLACE_ID, resultPlace.getId());
			assertEquals(PLACE_TITLE, resultPlace.getTitle());
		}

		@Test
		@DisplayName("Should handle LazyInitializationException")
		void shouldHandleLazyInitializationException() {
			// Given
			when(mockPlacelist.getPlaces()).thenThrow(LazyInitializationException.class);
			// When
			placelistEnrichmentService.enrichWithPlaces(mockPlacelist, testPlacelistDTO, PlacelistMode.WITH_PLACES);
			// Then
			assertNotNull(testPlacelistDTO.getPlaces());
			assertTrue(testPlacelistDTO.getPlaces().isEmpty());
		}

		@Test
		@DisplayName("Should not enrich in WITHOUT_PLACES mode")
		void shouldNotEnrichInWithoutPlacesMode() {
			// When
			placelistEnrichmentService.enrichWithPlaces(mockPlacelist, testPlacelistDTO, PlacelistMode.WITHOUT_PLACES);
			// Then
			assertNotNull(testPlacelistDTO.getPlaces());
			assertTrue(testPlacelistDTO.getPlaces().isEmpty());
		}
	}

	/** Top comments selection tests */
	@Nested
	@DisplayName("Top comments selection tests")
	class GetTopCommentsTests {

		@Test
		@DisplayName("Should return the 3 most recent comments")
		void shouldReturnTop3Comments() {
			// Given
			List<CommentPlacelist> manyComments = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				CommentPlacelist comment = new CommentPlacelist();
				comment.setId(i);
				comment.setCreationDate(Timestamp.valueOf("2026-01-" + (11 + i) + " 00:00:00.000"));
				manyComments.add(comment);
			}
			when(commentMapper.toDTO(any(CommentPlacelist.class))).thenAnswer(invocation -> {
				CommentPlacelist comment = invocation.getArgument(0);
				CommentDTO dto = new CommentDTO();
				dto.setId(comment.getId());
				dto.setCreationDate(comment.getCreationDate());
				return dto;
			});
			// When
			List<CommentDTO> result = placelistEnrichmentService.getTopComments(manyComments, 3);
			// Then
			assertNotNull(result);
			assertEquals(3, result.size());
			// Verify comments are sorted by descending creation date
			for (int i = 0; i < result.size() - 1; i++) {
				assertTrue(result.get(i).getCreationDate().after(result.get(i + 1).getCreationDate()));
			}
		}

		@Test
		@DisplayName("Should return an empty list for a null comment list")
		void shouldReturnEmptyListForNullComments() {
			// When
			List<CommentDTO> result = placelistEnrichmentService.getTopComments(null, 3);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return all comments when count is below the limit")
		void shouldReturnAllCommentsWhenBelowLimit() {
			// Given
			CommentDTO expectedDTO = new CommentDTO();
			expectedDTO.setId(COMMENT_ID);
			when(commentMapper.toDTO(testComments.get(0))).thenReturn(expectedDTO);
			// When
			List<CommentDTO> result = placelistEnrichmentService.getTopComments(testComments, 3);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
		}
	}
}
