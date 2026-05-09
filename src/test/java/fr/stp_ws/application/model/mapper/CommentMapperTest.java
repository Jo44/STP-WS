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

import fr.stp_ws.application.model.mapper.impl.CommentMapper;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.PlacelistUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;

/**
 * Comment mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Comment mapper tests")
@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

	private static final Integer COMMENT_ID = 1;
	private static final Float RATING = 4.5f;
	private static final String MESSAGE = "Test comment";
	private static final String USER_NAME = "Test User";
	private static final Timestamp CREATION_DATE = new Timestamp(System.currentTimeMillis());
	@InjectMocks
	private CommentMapper commentMapper;
	@Mock
	private User mockUser;
	private User testUser;
	private PlaceUser testPlace;
	private PlacelistUser testPlacelist;
	private Comment testComment;
	private CommentDTO testCommentDTO;

	/** Before each test */
	@BeforeEach
	void setUp() {
		// User initialization
		testUser = new User();
		testUser.setName(USER_NAME);
		// Place initialization
		testPlace = new PlaceUser();
		testPlace.setId(1);
		// Placelist initialization
		testPlacelist = new PlacelistUser();
		testPlacelist.setId(1);
		// Comment initialization
		testComment = new CommentPlace();
		testComment.setId(COMMENT_ID);
		testComment.setRating(RATING);
		testComment.setMessage(MESSAGE);
		testComment.setOwner(testUser);
		testComment.setCreationDate(CREATION_DATE);
		// Comment DTO initialization
		testCommentDTO = new CommentDTO();
		testCommentDTO.setId(COMMENT_ID);
		testCommentDTO.setRating(RATING);
		testCommentDTO.setMessage(MESSAGE);
		testCommentDTO.setOwner(USER_NAME);
		testCommentDTO.setCreationDate(CREATION_DATE);
	}

	/** Map comment to full DTO tests */
	@Nested
	@DisplayName("Map comment to full DTO tests")
	class ToDTOTests {

		@Test
		@DisplayName("Should convert a valid comment to DTO")
		void shouldConvertValidCommentToDTO() {
			// When
			CommentDTO result = commentMapper.toDTO(testComment);
			// Then
			assertNotNull(result);
			assertEquals(COMMENT_ID, result.getId());
			assertEquals(RATING, result.getRating());
			assertEquals(MESSAGE, result.getMessage());
			assertEquals(USER_NAME, result.getOwner());
			assertEquals(CREATION_DATE, result.getCreationDate());
		}

		@Test
		@DisplayName("Should return null for a null comment")
		void shouldReturnNullForNullComment() {
			// When
			CommentDTO result = commentMapper.toDTO(null);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should handle LazyInitializationException for user")
		void shouldHandleLazyInitializationExceptionForUser() {
			// Given
			Comment comment = new CommentPlace();
			comment.setId(COMMENT_ID);
			comment.setRating(RATING);
			comment.setMessage(MESSAGE);
			comment.setCreationDate(CREATION_DATE);
			when(mockUser.getName()).thenThrow(new LazyInitializationException("Test exception"));
			comment.setOwner(mockUser);
			// When
			CommentDTO result = commentMapper.toDTO(comment);
			// Then
			assertNotNull(result);
			assertEquals(COMMENT_ID, result.getId());
			assertEquals(RATING, result.getRating());
			assertEquals(MESSAGE, result.getMessage());
			assertEquals("User unknown", result.getOwner());
			assertEquals(CREATION_DATE, result.getCreationDate());
		}
	}

	/** Map comment list to DTO list tests */
	@Nested
	@DisplayName("Map comment list to DTO list tests")
	class ToDTOListTests {

		@Test
		@DisplayName("Should convert a list of valid comments to a list of DTOs")
		void shouldConvertValidCommentListToDTOList() {
			// Given
			List<Comment> comments = new ArrayList<>();
			comments.add(testComment);
			// When
			List<CommentDTO> result = commentMapper.toDTOList(comments);
			// Then
			assertNotNull(result);
			assertEquals(1, result.size());
			CommentDTO dto = result.get(0);
			assertEquals(COMMENT_ID, dto.getId());
			assertEquals(RATING, dto.getRating());
			assertEquals(MESSAGE, dto.getMessage());
			assertEquals(USER_NAME, dto.getOwner());
			assertEquals(CREATION_DATE, dto.getCreationDate());
		}

		@Test
		@DisplayName("Should return an empty list for a null list")
		void shouldReturnEmptyListForNullList() {
			// When
			List<CommentDTO> result = commentMapper.toDTOList(null);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}

		@Test
		@DisplayName("Should return an empty list for an empty list")
		void shouldReturnEmptyListForEmptyList() {
			// Given
			List<Comment> comments = new ArrayList<>();
			// When
			List<CommentDTO> result = commentMapper.toDTOList(comments);
			// Then
			assertNotNull(result);
			assertTrue(result.isEmpty());
		}
	}

	/** Map DTO to comment entity tests */
	@Nested
	@DisplayName("Map DTO to comment entity tests")
	class ToEntityTests {

		@Test
		@DisplayName("Should convert a DTO to CommentPlace")
		void shouldConvertDTOToCommentPlace() {
			// When
			Comment result = commentMapper.toEntity(testCommentDTO, testUser, testPlace, Place.class);
			// Then
			assertNotNull(result);
			assertTrue(result instanceof CommentPlace);
			CommentPlace commentPlace = (CommentPlace) result;
			assertEquals(COMMENT_ID, commentPlace.getId());
			assertEquals(RATING, commentPlace.getRating());
			assertEquals(MESSAGE, commentPlace.getMessage());
			assertEquals(testUser, commentPlace.getOwner());
			assertEquals(testPlace, commentPlace.getPlace());
			assertEquals(CREATION_DATE, commentPlace.getCreationDate());
			assertNotNull(commentPlace.getLastUpdate());
		}

		@Test
		@DisplayName("Should convert a DTO to CommentPlacelist")
		void shouldConvertDTOToCommentPlacelist() {
			// When
			Comment result = commentMapper.toEntity(testCommentDTO, testUser, testPlacelist, Placelist.class);
			// Then
			assertNotNull(result);
			assertTrue(result instanceof CommentPlacelist);
			CommentPlacelist commentPlacelist = (CommentPlacelist) result;
			assertEquals(COMMENT_ID, commentPlacelist.getId());
			assertEquals(RATING, commentPlacelist.getRating());
			assertEquals(MESSAGE, commentPlacelist.getMessage());
			assertEquals(testUser, commentPlacelist.getOwner());
			assertEquals(testPlacelist, commentPlacelist.getPlacelist());
			assertEquals(CREATION_DATE, commentPlacelist.getCreationDate());
			assertNotNull(commentPlacelist.getLastUpdate());
		}

		@Test
		@DisplayName("Should return null for a null DTO")
		void shouldReturnNullForNullDTO() {
			// When
			Comment result = commentMapper.toEntity(null, testUser, testPlace, Place.class);
			// Then
			assertNull(result);
		}

		@Test
		@DisplayName("Should set creation date if null in DTO")
		void shouldSetCreationDateIfNullInDTO() {
			// Given
			testCommentDTO.setCreationDate(null);
			// When
			Comment result = commentMapper.toEntity(testCommentDTO, testUser, testPlace, Place.class);
			// Then
			assertNotNull(result);
			assertNotNull(result.getCreationDate());
		}
	}
}
