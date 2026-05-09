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
import fr.stp_ws.application.model.mapper.inter.IPhotoMapper;
import fr.stp_ws.application.service.impl.PlaceEnrichmentService;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;

/**
 * Place enrichment service tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Place enrichment service tests")
@ExtendWith(MockitoExtension.class)
class PlaceEnrichmentServiceTest {

	private static final Integer COMMENT_ID = 1;
	private static final Integer PHOTO_ID = 1;
	private static final String USER_NAME = "Test User";
	private static final Float RATING = 4.5f;
	private static final String COMMENT_TEXT = "Test comment";
	private static final String PHOTO_URL = "http://example.com/photo.jpg";
	private static final Timestamp CREATION_DATE = new Timestamp(System.currentTimeMillis());
	private static final Timestamp UPLOAD_DATE = new Timestamp(System.currentTimeMillis());
	@InjectMocks
	private PlaceEnrichmentService placeEnrichmentService;
	@Mock
	private ICommentMapper commentMapper;
	@Mock
	private IPhotoMapper photoMapper;
	@Mock
	private Place mockPlace;
	private PlaceDTO testPlaceDTO;
	private List<CommentPlace> testComments;
	private List<Photo> testPhotos;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		User testUser = new User();
		testUser.setName(USER_NAME);
		// Comments initialization
		testComments = new ArrayList<>();
		CommentPlace comment = new CommentPlace();
		comment.setId(COMMENT_ID);
		comment.setOwner(testUser);
		comment.setRating(RATING);
		comment.setMessage(COMMENT_TEXT);
		comment.setCreationDate(CREATION_DATE);
		testComments.add(comment);
		// Photos initialization
		testPhotos = new ArrayList<>();
		Photo photo = new Photo();
		photo.setId(PHOTO_ID);
		photo.setUrl(PHOTO_URL);
		photo.setUploadDate(UPLOAD_DATE);
		photo.setPlace(mockPlace);
		testPhotos.add(photo);
		// Place DTO initialization
		testPlaceDTO = new PlaceDTO();
	}

	/** Enrich place with comments tests */
	@Nested
	@DisplayName("Enrich place with comments tests")
	class EnrichWithCommentsTests {

		@Test
		@DisplayName("Should enrich with comments in MAX_3 mode")
		void shouldEnrichWithCommentsInMax3Mode() {
			// Given
			when(mockPlace.getComments()).thenReturn(testComments);
			CommentDTO commentDTO = new CommentDTO();
			commentDTO.setId(COMMENT_ID);
			commentDTO.setOwner(USER_NAME);
			commentDTO.setRating(RATING);
			commentDTO.setMessage(COMMENT_TEXT);
			commentDTO.setCreationDate(CREATION_DATE);
			when(commentMapper.toDTO(testComments.get(0))).thenReturn(commentDTO);
			// When
			placeEnrichmentService.enrichWithComments(mockPlace, testPlaceDTO, CommentMode.MAX_3);
			// Then
			assertNotNull(testPlaceDTO.getComments());
			assertEquals(1, testPlaceDTO.getComments().size());
			CommentDTO resultComment = testPlaceDTO.getComments().get(0);
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
			when(mockPlace.getComments()).thenThrow(LazyInitializationException.class);
			// When
			placeEnrichmentService.enrichWithComments(mockPlace, testPlaceDTO, CommentMode.MAX_3);
			// Then
			assertNotNull(testPlaceDTO.getComments());
			assertTrue(testPlaceDTO.getComments().isEmpty());
		}

		@Test
		@DisplayName("Should not enrich in NONE mode")
		void shouldNotEnrichInNoneMode() {
			// When
			placeEnrichmentService.enrichWithComments(mockPlace, testPlaceDTO, CommentMode.NONE);
			// Then
			assertNotNull(testPlaceDTO.getComments());
			assertTrue(testPlaceDTO.getComments().isEmpty());
		}
	}

	/** Enrich place with photos tests */
	@Nested
	@DisplayName("Enrich place with photos tests")
	class EnrichWithPhotosTests {

		@Test
		@DisplayName("Should enrich with photos in MAX_3 mode")
		void shouldEnrichWithPhotosInMax3Mode() {
			// Given
			when(mockPlace.getPhotos()).thenReturn(testPhotos);
			PhotoDTO photoDTO = new PhotoDTO();
			photoDTO.setId(PHOTO_ID);
			photoDTO.setUrl(PHOTO_URL);
			photoDTO.setUploadDate(UPLOAD_DATE);
			when(photoMapper.toDTO(testPhotos.get(0))).thenReturn(photoDTO);
			// When
			placeEnrichmentService.enrichWithPhotos(mockPlace, testPlaceDTO, PhotoMode.MAX_3);
			// Then
			assertNotNull(testPlaceDTO.getPhotos());
			assertEquals(1, testPlaceDTO.getPhotos().size());
			PhotoDTO resultPhoto = testPlaceDTO.getPhotos().get(0);
			assertEquals(PHOTO_ID, resultPhoto.getId());
			assertEquals(PHOTO_URL, resultPhoto.getUrl());
			assertEquals(UPLOAD_DATE, resultPhoto.getUploadDate());
		}

		@Test
		@DisplayName("Should handle LazyInitializationException")
		void shouldHandleLazyInitializationException() {
			// Given
			when(mockPlace.getPhotos()).thenThrow(LazyInitializationException.class);
			// When
			placeEnrichmentService.enrichWithPhotos(mockPlace, testPlaceDTO, PhotoMode.MAX_3);
			// Then
			assertNotNull(testPlaceDTO.getPhotos());
			assertTrue(testPlaceDTO.getPhotos().isEmpty());
		}

		@Test
		@DisplayName("Should not enrich in NONE mode")
		void shouldNotEnrichInNoneMode() {
			// When
			placeEnrichmentService.enrichWithPhotos(mockPlace, testPlaceDTO, PhotoMode.NONE);
			// Then
			assertNotNull(testPlaceDTO.getPhotos());
			assertTrue(testPlaceDTO.getPhotos().isEmpty());
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
			List<CommentPlace> manyComments = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				CommentPlace comment = new CommentPlace();
				comment.setId(i);
				comment.setCreationDate(Timestamp.valueOf("2026-01-" + (11 + i) + " 00:00:00.000"));
				manyComments.add(comment);
			}
			when(commentMapper.toDTO(any(CommentPlace.class))).thenAnswer(invocation -> {
				CommentPlace comment = invocation.getArgument(0);
				CommentDTO dto = new CommentDTO();
				dto.setId(comment.getId());
				dto.setCreationDate(comment.getCreationDate());
				return dto;
			});
			// When
			List<CommentDTO> result = placeEnrichmentService.getTopComments(manyComments, 3);
			// Then
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
			List<CommentDTO> result = placeEnrichmentService.getTopComments(null, 3);
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
			List<CommentDTO> result = placeEnrichmentService.getTopComments(testComments, 3);
			// Then
			assertEquals(testComments.size(), result.size());
			assertEquals(expectedDTO.getId(), result.get(0).getId());
		}
	}

	/** Top photos selection tests */
	@Nested
	@DisplayName("Top photos selection tests")
	class GetTopPhotosTests {

		@Test
		@DisplayName("Should return the 3 most recent photos")
		void shouldReturnTop3Photos() {
			// Given
			List<Photo> manyPhotos = new ArrayList<>();
			for (int i = 0; i < 5; i++) {
				Photo photo = new Photo();
				photo.setId(i);
				photo.setUploadDate(Timestamp.valueOf("2026-01-" + (11 + i) + " 00:00:00.000"));
				manyPhotos.add(photo);
			}
			when(photoMapper.toDTO(any(Photo.class))).thenAnswer(invocation -> {
				Photo photo = invocation.getArgument(0);
				PhotoDTO dto = new PhotoDTO();
				dto.setId(photo.getId());
				dto.setUploadDate(photo.getUploadDate());
				return dto;
			});
			// When
			List<PhotoDTO> result = placeEnrichmentService.getTopPhotos(manyPhotos, 3);
			// Then
			assertEquals(3, result.size());
			// Verify photos are sorted by descending upload date
			for (int i = 0; i < result.size() - 1; i++) {
				assertTrue(result.get(i).getUploadDate().after(result.get(i + 1).getUploadDate()));
			}
		}

		@Test
		@DisplayName("Should return an empty list for a null photo list")
		void shouldReturnEmptyListForNullPhotos() {
			// When
			List<PhotoDTO> result = placeEnrichmentService.getTopPhotos(null, 3);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return all photos when count is below the limit")
		void shouldReturnAllPhotosWhenBelowLimit() {
			// Given
			PhotoDTO expectedDTO = new PhotoDTO();
			expectedDTO.setId(PHOTO_ID);
			when(photoMapper.toDTO(testPhotos.get(0))).thenReturn(expectedDTO);
			// When
			List<PhotoDTO> result = placeEnrichmentService.getTopPhotos(testPhotos, 3);
			// Then
			assertEquals(testPhotos.size(), result.size());
			assertEquals(expectedDTO.getId(), result.get(0).getId());
		}
	}
}
