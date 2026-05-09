package fr.stp_ws.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistPhotoException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import jakarta.persistence.PersistenceException;

/**
 * Photo repository tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Photo repository tests")
class PhotoRepoTest {

	private static Settings settings;
	private static Hibernate hibernate;
	private PhotoRepo photoRepo;
	private LoadingService loadingService;
	private User testUser;
	private Place testPlace;
	private Photo testPhoto;

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
		// Photo repository
		photoRepo = new PhotoRepo(hibernate, settings);
		// Manual dependency injection
		try {
			var field = PhotoRepo.class.getDeclaredField("loadingService");
			field.setAccessible(true);
			field.set(photoRepo, loadingService);
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
		// Timestamp
		Timestamp now = new Timestamp(System.currentTimeMillis());
		// Create a photo
		testPhoto = new Photo();
		testPhoto.setUrl("https://example.com/test.jpg");
		testPhoto.setDescription("Test photo description");
		testPhoto.setUploadDate(now);
		testPhoto.setPlace(testPlace);
		// Persist test entities
		var session = hibernate.openSession();
		try {
			session.persist(testUser);
			session.persist(testPlace);
			session.persist(testPhoto);
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
			// Delete photos
			session.createMutationQuery("DELETE FROM Photo").executeUpdate();
			// Delete places
			session.createMutationQuery("DELETE FROM PlaceUser").executeUpdate();
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

	/** Get all photos tests */
	@Nested
	@DisplayName("Get all photos tests")
	class GetAllTests {

		@Test
		@DisplayName("Should return a list of photos for a place")
		void shouldReturnPhotosListForPlace() throws TechnicalException, FunctionalException {
			// When
			List<Photo> photos = photoRepo.getAll(testPlace.getId(), testUser.getId());
			// Then
			assertNotNull(photos);
			assertEquals(1, photos.size());
			assertEquals(testPhoto.getId(), photos.get(0).getId());
		}
	}

	/** Get photo by id tests */
	@Nested
	@DisplayName("Get photo by id tests")
	class GetTests {

		@Test
		@DisplayName("Should return an existing photo")
		void shouldReturnExistingPhoto() throws FunctionalException, TechnicalException {
			// When
			Photo photo = photoRepo.get(testPhoto.getId());
			// Then
			assertNotNull(photo);
			assertEquals(testPhoto.getId(), photo.getId());
		}

		@Test
		@DisplayName("Should throw NotExistPhotoException when photo does not exist")
		void shouldThrowNotExistPhotoExceptionWhenPhotoDoesNotExist() throws FunctionalException {
			// When & Then
			NotExistPhotoException nepex = assertThrows(NotExistPhotoException.class, () -> photoRepo.get(999));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Add photo tests */
	@Nested
	@DisplayName("Add photo tests")
	class AddTests {

		@Test
		@DisplayName("Should add a photo to a place")
		void shouldAddPhotoToPlace() throws FunctionalException, TechnicalException {
			// Given
			Photo newPhoto = new Photo();
			Place appPlace = testPlace;
			newPhoto.setUrl("https://example.com/new.jpg");
			newPhoto.setDescription("New test photo");
			newPhoto.setUploadDate(new Timestamp(System.currentTimeMillis()));
			newPhoto.setPlace(appPlace);
			// When
			Photo addedPhoto = photoRepo.add(newPhoto);
			// Then
			assertNotNull(addedPhoto);
			assertEquals(newPhoto.getUrl(), addedPhoto.getUrl());
		}
	}

	/** Delete photo tests */
	@Nested
	@DisplayName("Delete photo tests")
	class DeleteTests {

		@Test
		@DisplayName("Should delete a photo")
		void shouldDeletePhoto() throws FunctionalException, TechnicalException {
			// When
			Photo deletedPhoto = photoRepo.delete(testPhoto.getId());
			// Then
			assertNotNull(deletedPhoto);
			assertEquals(testPhoto.getId(), deletedPhoto.getId());
		}

		@Test
		@DisplayName("Should throw NotExistPhotoException when photo does not exist")
		void shouldThrowNotExistPhotoExceptionWhenPhotoDoesNotExist() throws FunctionalException {
			// When & Then
			NotExistPhotoException nepex = assertThrows(NotExistPhotoException.class, () -> photoRepo.delete(999));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Count photos tests */
	@Nested
	@DisplayName("Count photos tests")
	class CountTests {

		@Test
		@DisplayName("Should return the number of photos for a place")
		void shouldReturnPhotoCount() throws TechnicalException {
			// When
			Integer count = photoRepo.count(testPlace.getId());
			// Then
			assertNotNull(count);
			assertEquals(1, count);
		}
	}
}
