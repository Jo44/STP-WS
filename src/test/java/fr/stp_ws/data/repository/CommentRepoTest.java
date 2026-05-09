package fr.stp_ws.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Timestamp;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.HibernateUtils;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.PlacelistUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistCommentException;
import fr.stp_ws.domain.exception.NotExistPlaceException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import jakarta.persistence.PersistenceException;

/**
 * Comment repository tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Comment repository tests")
class CommentRepoTest {

	private static Settings settings;
	private static Hibernate hibernate;
	private CommentRepo commentRepo;
	private LoadingService loadingService;
	private User testUser;
	private Place testPlace;
	private Placelist testPlacelist;
	private CommentPlace testCommentPlace;
	private CommentPlacelist testCommentPlacelist;

	/** Before all tests */
	@BeforeAll
	static void setUpClass() throws TechnicalException {
		// Initialize settings and Hibernate
		HibernateUtils.initialize();
		settings = HibernateUtils.getSettings();
		hibernate = HibernateUtils.getHibernate();
	}

	/** After all tests */
	@AfterAll
	static void tearDownClass() {
		// Shut down Hibernate
		HibernateUtils.shutdown();
	}

	/** Before each test */
	@BeforeEach
	void setUp() throws TechnicalException {
		// Loading service
		loadingService = new LoadingService();
		// Comment repository
		commentRepo = new CommentRepo(hibernate, settings);
		// Manual dependency injection
		try {
			var field = CommentRepo.class.getDeclaredField("loadingService");
			field.setAccessible(true);
			field.set(commentRepo, loadingService);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException ex) {
			throw new RuntimeException("Error during dependency injection");
		}
		// Create a user
		testUser = new User();
		testUser.setName("Test User");
		testUser.setEmail("test@example.com");
		testUser.setTourist(false);
		testUser.setGoogle(false);
		testUser.setSecret("test_secret");
		testUser.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testUser.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		// Create a place
		testPlace = new PlaceUser();
		testPlace.setOwner(testUser);
		testPlace.setTitle("Test Place");
		testPlace.setVisibility(true);
		testPlace.setType(EntityType.EAT);
		testPlace.setTimes("10:00-22:00");
		testPlace.setLatitude(48.8566);
		testPlace.setLongitude(2.3522);
		testPlace.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testPlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		// Create a placelist
		testPlacelist = new PlacelistUser();
		testPlacelist.setOwner(testUser);
		testPlacelist.setTitle("Test Placelist");
		testPlacelist.setVisibility(true);
		testPlacelist.setType(EntityType.EAT);
		testPlacelist.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testPlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		// Timestamp
		Timestamp now = new Timestamp(System.currentTimeMillis());
		// Create a place comment
		testCommentPlace = new CommentPlace();
		testCommentPlace.setOwner(testUser);
		testCommentPlace.setPlace(testPlace);
		testCommentPlace.setRating(4.5f);
		testCommentPlace.setMessage("Test comment for place");
		testCommentPlace.setCreationDate(now);
		testCommentPlace.setLastUpdate(now);
		// Create a placelist comment
		testCommentPlacelist = new CommentPlacelist();
		testCommentPlacelist.setOwner(testUser);
		testCommentPlacelist.setPlacelist(testPlacelist);
		testCommentPlacelist.setRating(4.0f);
		testCommentPlacelist.setMessage("Test comment for placelist");
		testCommentPlacelist.setCreationDate(now);
		testCommentPlacelist.setLastUpdate(now);
		// Persist test entities
		var session = hibernate.openSession();
		try {
			session.persist(testUser);
			session.persist(testPlace);
			session.persist(testPlacelist);
			session.persist(testCommentPlace);
			session.persist(testCommentPlacelist);
			session.flush();
			hibernate.commit(session);
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			throw new RuntimeException("Error during test data persistence");
		}
	}

	/** After each test */
	@AfterEach
	void tearDown() throws TechnicalException {
		var session = hibernate.openSession();
		try {
			// Delete comments
			session.createMutationQuery("DELETE FROM CommentPlace").executeUpdate();
			session.createMutationQuery("DELETE FROM CommentPlacelist").executeUpdate();
			// Delete places and placelists
			session.createMutationQuery("DELETE FROM PlaceUser").executeUpdate();
			session.createMutationQuery("DELETE FROM PlacelistUser").executeUpdate();
			// Delete users
			session.createMutationQuery("DELETE FROM User").executeUpdate();
			session.flush();
			hibernate.commit(session);
		} catch (PersistenceException pex) {
			throw new RuntimeException("Error during test data cleanup");
		} finally {
			hibernate.rollback(session);
		}
	}

	/** Get all comments tests */
	@Nested
	@DisplayName("Get all comments tests")
	class GetAllTests {

		@Test
		@DisplayName("Should return a list of comments for a place")
		void shouldReturnCommentsListForPlace() throws TechnicalException {
			// When
			List<Comment> comments = commentRepo.getAll(testPlace.getId(), testUser.getId(), Place.class);
			// Then
			assertNotNull(comments);
		}

		@Test
		@DisplayName("Should return a list of comments for a placelist")
		void shouldReturnCommentsListForPlacelist() throws TechnicalException {
			// When
			List<Comment> comments = commentRepo.getAll(testPlacelist.getId(), testUser.getId(), Placelist.class);
			// Then
			assertNotNull(comments);
		}
	}

	/** Get comment by id tests */
	@Nested
	@DisplayName("Get comment by id tests")
	class GetTests {

		@Test
		@DisplayName("Should return an existing place comment")
		void shouldReturnExistingPlaceComment() throws FunctionalException, TechnicalException {
			// When
			Comment comment = commentRepo.get(testCommentPlace.getId(), Place.class);
			// Then
			assertNotNull(comment);
			assertEquals(testCommentPlace.getId(), comment.getId());
		}

		@Test
		@DisplayName("Should return an existing placelist comment")
		void shouldReturnExistingPlacelistComment() throws FunctionalException, TechnicalException {
			// When
			Comment comment = commentRepo.get(testCommentPlacelist.getId(), Placelist.class);
			// Then
			assertNotNull(comment);
			assertEquals(testCommentPlacelist.getId(), comment.getId());
		}

		@Test
		@DisplayName("Should throw NotExistCommentException when comment does not exist")
		void shouldThrowNotExistCommentExceptionWhenCommentDoesNotExist() {
			// When & Then
			NotExistCommentException necex = assertThrows(NotExistCommentException.class,
					() -> commentRepo.get(999, Place.class));
			assertNotNull(necex.getMessage());
		}
	}

	/** Add comment tests */
	@Nested
	@DisplayName("Add comment tests")
	class AddTests {

		@Test
		@DisplayName("Should add a comment to a place")
		void shouldAddCommentToPlace() throws FunctionalException, TechnicalException {
			// Given
			CommentPlace newComment = new CommentPlace();
			newComment.setOwner(testUser);
			newComment.setPlace(testPlace);
			newComment.setRating(4.0f);
			newComment.setMessage("New test comment");
			newComment.setCreationDate(new Timestamp(System.currentTimeMillis()));
			newComment.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// When
			Comment addedComment = commentRepo.add(testPlace.getId(), newComment, Place.class);
			// Then
			assertNotNull(addedComment);
			assertEquals(newComment.getMessage(), addedComment.getMessage());
		}

		@Test
		@DisplayName("Should add a comment to a placelist")
		void shouldAddCommentToPlacelist() throws FunctionalException, TechnicalException {
			// Given
			CommentPlacelist newComment = new CommentPlacelist();
			newComment.setOwner(testUser);
			newComment.setPlacelist(testPlacelist);
			newComment.setRating(4.0f);
			newComment.setMessage("New test comment");
			newComment.setCreationDate(new Timestamp(System.currentTimeMillis()));
			newComment.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// When
			Comment addedComment = commentRepo.add(testPlacelist.getId(), newComment, Placelist.class);
			// Then
			assertNotNull(addedComment);
			assertEquals(newComment.getMessage(), addedComment.getMessage());
		}

		@Test
		@DisplayName("Should throw NotExistPlaceException when place does not exist")
		void shouldThrowNotExistPlaceExceptionWhenPlaceDoesNotExist() {
			// Given
			CommentPlace newComment = new CommentPlace();
			newComment.setOwner(testUser);
			newComment.setRating(4.0f);
			newComment.setMessage("Test comment");
			// When & Then
			NotExistPlaceException nepex = assertThrows(NotExistPlaceException.class,
					() -> commentRepo.add(999, newComment, Place.class));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Delete comment tests */
	@Nested
	@DisplayName("Delete comment tests")
	class DeleteTests {

		@Test
		@DisplayName("Should delete a place comment")
		void shouldDeletePlaceComment() throws FunctionalException, TechnicalException {
			// When
			Comment deletedComment = commentRepo.delete(testCommentPlace.getId(), Place.class);
			// Then
			assertNotNull(deletedComment);
			assertEquals(testCommentPlace.getId(), deletedComment.getId());
		}

		@Test
		@DisplayName("Should delete a placelist comment")
		void shouldDeletePlacelistComment() throws FunctionalException, TechnicalException {
			// When
			Comment deletedComment = commentRepo.delete(testCommentPlacelist.getId(), Placelist.class);
			// Then
			assertNotNull(deletedComment);
			assertEquals(testCommentPlacelist.getId(), deletedComment.getId());
		}

		@Test
		@DisplayName("Should throw NotExistCommentException when comment does not exist")
		void shouldThrowNotExistCommentExceptionWhenCommentDoesNotExist() {
			// When & Then
			NotExistCommentException necex = assertThrows(NotExistCommentException.class,
					() -> commentRepo.delete(999, Place.class));
			assertNotNull(necex.getMessage());
		}
	}

	/** User has commented tests */
	@Nested
	@DisplayName("User has commented tests")
	class HasCommentedTests {

		@Test
		@DisplayName("Should return true when user has already commented the place")
		void shouldReturnTrueWhenUserHasCommentedPlace() throws TechnicalException {
			// When
			Boolean hasCommented = commentRepo.hasCommented(testPlace.getId(), testUser.getId(), Place.class);
			// Then
			assertTrue(hasCommented);
		}

		@Test
		@DisplayName("Should return true when user has already commented the placelist")
		void shouldReturnTrueWhenUserHasCommentedPlacelist() throws TechnicalException {
			// When
			Boolean hasCommented = commentRepo.hasCommented(testPlacelist.getId(), testUser.getId(), Placelist.class);
			// Then
			assertTrue(hasCommented);
		}

		@Test
		@DisplayName("Should return false when user has not commented the place")
		void shouldReturnFalseWhenUserHasNotCommentedPlace() throws TechnicalException {
			// Given
			User otherUser = new User();
			otherUser.setName("Other User");
			otherUser.setEmail("other@example.com");
			otherUser.setTourist(false);
			otherUser.setGoogle(false);
			otherUser.setSecret("other_secret");
			otherUser.setCreationDate(new Timestamp(System.currentTimeMillis()));
			otherUser.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// Persist another user
			var session = hibernate.openSession();
			try {
				session.persist(otherUser);
				hibernate.commit(session);
			} finally {
				hibernate.rollback(session);
			}
			// When
			Boolean hasCommented = commentRepo.hasCommented(testPlace.getId(), otherUser.getId(), Place.class);
			// Then
			assertFalse(hasCommented);
		}
	}
}
