package fr.stp_ws.application.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import fr.stp_ws.application.model.mapper.impl.PhotoMapper;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;

/**
 * Photo mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Photo mapper tests")
@ExtendWith(MockitoExtension.class)
class PhotoMapperTest {

	private static final Integer PHOTO_ID = 1;
	private static final Integer PLACE_ID = 1;
	private static final String URL = "http://example.com/photo.jpg";
	private static final String DESCRIPTION = "Test photo";
	private static final Timestamp UPLOAD_DATE = new Timestamp(System.currentTimeMillis());
	@InjectMocks
	private PhotoMapper photoMapper;
	@Mock
	private Place mockPlace;
	private PlaceUser testPlace;
	private Photo testPhoto;
	private PhotoDTO testPhotoDTO;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// Place initialization
		testPlace = new PlaceUser();
		testPlace.setId(PLACE_ID);
		// Photo initialization
		testPhoto = new Photo();
		testPhoto.setId(PHOTO_ID);
		testPhoto.setUrl(URL);
		testPhoto.setDescription(DESCRIPTION);
		testPhoto.setUploadDate(UPLOAD_DATE);
		testPhoto.setPlace(testPlace);
		// Photo DTO initialization
		testPhotoDTO = new PhotoDTO();
		testPhotoDTO.setId(PHOTO_ID);
		testPhotoDTO.setPlaceId(PLACE_ID);
		testPhotoDTO.setUrl(URL);
		testPhotoDTO.setDescription(DESCRIPTION);
		testPhotoDTO.setUploadDate(UPLOAD_DATE);
	}

	/** Map photo to full DTO tests */
	@Nested
	@DisplayName("Map photo to full DTO tests")
	class ToDTOTests {

		@Test
		@DisplayName("Should convert a valid photo to DTO")
		void shouldConvertValidPhotoToDTO() {
			// When
			PhotoDTO result = photoMapper.toDTO(testPhoto);
			// Then
			assertNotNull(result);
			assertEquals(PHOTO_ID, result.getId());
			assertEquals(PLACE_ID, result.getPlaceId());
			assertEquals(URL, result.getUrl());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(UPLOAD_DATE, result.getUploadDate());
		}

		@Test
		@DisplayName("Should return null for a null photo")
		void shouldReturnNullForNullPhoto() {
			// When
			PhotoDTO result = photoMapper.toDTO(null);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should handle LazyInitializationException for place")
		void shouldHandleLazyInitializationExceptionForPlace() {
			// Given
			Photo photo = new Photo();
			photo.setId(PHOTO_ID);
			photo.setUrl(URL);
			photo.setDescription(DESCRIPTION);
			photo.setUploadDate(UPLOAD_DATE);
			when(mockPlace.getId()).thenThrow(new LazyInitializationException("Test exception"));
			photo.setPlace(mockPlace);
			// When
			PhotoDTO result = photoMapper.toDTO(photo);
			// Then
			assertNotNull(result);
			assertEquals(PHOTO_ID, result.getId());
			assertEquals(-1, result.getPlaceId());
			assertEquals(URL, result.getUrl());
			assertEquals(DESCRIPTION, result.getDescription());
			assertEquals(UPLOAD_DATE, result.getUploadDate());
		}
	}

	/** Map photo list to DTO list tests */
	@Nested
	@DisplayName("Map photo list to DTO list tests")
	class ToDTOListTests {

		@Test
		@DisplayName("Should convert a list of valid photos to a list of DTOs")
		void shouldConvertValidPhotoListToDTOList() {
			// Given
			List<Photo> photos = new ArrayList<>();
			photos.add(testPhoto);
			// When
			List<PhotoDTO> result = photoMapper.toDTOList(photos);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			PhotoDTO dto = result.get(0);
			assertEquals(PHOTO_ID, dto.getId());
			assertEquals(PLACE_ID, dto.getPlaceId());
			assertEquals(URL, dto.getUrl());
			assertEquals(DESCRIPTION, dto.getDescription());
			assertEquals(UPLOAD_DATE, dto.getUploadDate());
		}

		@Test
		@DisplayName("Should return an empty list for a null list")
		void shouldReturnEmptyListForNullList() {
			// When
			List<PhotoDTO> result = photoMapper.toDTOList(null);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for an empty list")
		void shouldReturnEmptyListForEmptyList() {
			// Given
			List<Photo> photos = new ArrayList<>();
			// When
			List<PhotoDTO> result = photoMapper.toDTOList(photos);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}
	}

	/** Map DTO to photo entity tests */
	@Nested
	@DisplayName("Map DTO to photo entity tests")
	class ToEntityTests {

		@Test
		@DisplayName("Should convert a DTO to Photo")
		void shouldConvertDTOToPhoto() {
			// When
			Photo result = photoMapper.toEntity(testPhotoDTO, testPlace);
			// Then
			assertNotNull(result);
			assertEquals(PHOTO_ID, result.getId());
			assertEquals(URL, result.getUrl());
			assertEquals(DESCRIPTION, result.getDescription());
			assertNotNull(result.getUploadDate());
			assertTrue(result.getUploadDate().after(UPLOAD_DATE));
			assertEquals(testPlace, result.getPlace());
		}

		@Test
		@DisplayName("Should return null for a null DTO")
		void shouldReturnNullForNullDTO() {
			// When
			Photo result = photoMapper.toEntity(null, testPlace);
			// Then
			assertNull(result);
		}
	}
}
