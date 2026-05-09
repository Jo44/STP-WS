package fr.stp_ws.data.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Timestamp;
import java.util.ArrayList;
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
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistPlaceException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import jakarta.persistence.PersistenceException;

/**
 * Place repository tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Place repository tests")
class PlaceRepoTest {

	private static Settings settings;
	private static Hibernate hibernate;
	private PlaceRepo placeRepo;
	private LoadingService loadingService;
	private User testUser;
	private Place testPlace;

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
		// Place repository
		placeRepo = new PlaceRepo(hibernate, settings);
		// Manual dependency injection
		try {
			var field = PlaceRepo.class.getDeclaredField("loadingService");
			field.setAccessible(true);
			field.set(placeRepo, loadingService);
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
		testUser.setPlaces(new ArrayList<>());
		testUser.setPlacelists(new ArrayList<>());
		testUser.setComments(new ArrayList<>());
		// Create a place
		testPlace = new PlaceUser();
		testPlace.setOwner(testUser);
		testPlace.setTitle("Test Place");
		testPlace.setType(EntityType.EAT);
		testPlace.setTimes("10:00-22:00");
		testPlace.setLatitude(48.8566);
		testPlace.setLongitude(2.3522);
		testPlace.setVisibility(true);
		testPlace.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testPlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		testPlace.setComments(new ArrayList<>());
		testPlace.setPhotos(new ArrayList<>());
		// Persist test entities
		var session = hibernate.openSession();
		try {
			session.persist(testUser);
			session.persist(testPlace);
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

	/** Get all places tests */
	@Nested
	@DisplayName("Get all places tests")
	class GetAllTests {

		@Test
		@DisplayName("Should return the user's place list")
		void shouldReturnUserPlacesList() throws FunctionalException, TechnicalException {
			// When
			List<Place> places = placeRepo.getAll(EntityCategory.USER, true, testUser.getId(), null, 48.0, 49.0, 2.0,
					3.0);
			// Then
			assertNotNull(places);
			assertEquals(1, places.size());
			assertEquals(testPlace.getId(), places.get(0).getId());
		}

		@Test
		@DisplayName("Should return the public place list")
		void shouldReturnPublicPlacesList() throws FunctionalException, TechnicalException {
			// When
			List<Place> places = placeRepo.getAll(EntityCategory.USER, false, testUser.getId(), null, 48.0, 49.0, 2.0,
					3.0);
			// Then
			assertNotNull(places);
			assertEquals(1, places.size());
			assertEquals(testPlace.getId(), places.get(0).getId());
		}

		@Test
		@DisplayName("Should return the place list filtered by type")
		void shouldReturnPlacesListFilteredByType() throws FunctionalException, TechnicalException {
			// When
			List<Place> places = placeRepo.getAll(EntityCategory.USER, true, testUser.getId(), EntityType.EAT, 48.0,
					49.0, 2.0, 3.0);
			// Then
			assertNotNull(places);
			assertEquals(1, places.size());
			assertEquals(testPlace.getId(), places.get(0).getId());
			assertEquals(EntityType.EAT, places.get(0).getType());
		}

		@Test
		@DisplayName("Should not expose user B private place to user A")
		void shouldNotExposeUserBPrivatePlaceToUserA() throws FunctionalException, TechnicalException {
			// Given
			User otherUser = new User();
			otherUser.setName("Other User");
			otherUser.setEmail("other@example.com");
			otherUser.setTourist(false);
			otherUser.setGoogle(false);
			otherUser.setSecret("other_secret");
			otherUser.setCreationDate(new Timestamp(System.currentTimeMillis()));
			otherUser.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			otherUser.setPlaces(new ArrayList<>());
			otherUser.setPlacelists(new ArrayList<>());
			otherUser.setComments(new ArrayList<>());
			Place otherPrivatePlace = new PlaceUser();
			otherPrivatePlace.setOwner(otherUser);
			otherPrivatePlace.setTitle("Other Private Place");
			otherPrivatePlace.setType(EntityType.EAT);
			otherPrivatePlace.setTimes("09:00-19:00");
			otherPrivatePlace.setLatitude(48.8570);
			otherPrivatePlace.setLongitude(2.3530);
			otherPrivatePlace.setVisibility(false);
			otherPrivatePlace.setCreationDate(new Timestamp(System.currentTimeMillis()));
			otherPrivatePlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			otherPrivatePlace.setComments(new ArrayList<>());
			otherPrivatePlace.setPhotos(new ArrayList<>());
			var session = hibernate.openSession();
			try {
				session.persist(otherUser);
				session.persist(otherPrivatePlace);
				session.flush();
				hibernate.commit(session);
			} catch (PersistenceException pex) {
				hibernate.rollback(session);
				throw new RuntimeException("Error during additional test data persistence");
			}
			// When
			List<Place> placesVisibleToUserA = placeRepo.getAll(EntityCategory.USER, false, testUser.getId(), null,
					48.0, 49.0, 2.0, 3.0);
			// Then
			assertNotNull(placesVisibleToUserA);
			assertFalse(placesVisibleToUserA.stream()
					.anyMatch(place -> place.getId() != null && place.getId().equals(otherPrivatePlace.getId())));
		}
	}

	/** Get place by id tests */
	@Nested
	@DisplayName("Get place by id tests")
	class GetTests {

		@Test
		@DisplayName("Should return an existing place")
		void shouldReturnExistingPlace() throws FunctionalException, TechnicalException {
			// When
			Place place = placeRepo.get(testPlace.getId(), CommentMode.NONE, PhotoMode.NONE);
			// Then
			assertNotNull(place);
			assertEquals(testPlace.getId(), place.getId());
			assertEquals(testPlace.getTitle(), place.getTitle());
			assertEquals(testPlace.getType(), place.getType());
		}

		@Test
		@DisplayName("Should throw NotExistPlaceException when place does not exist")
		void shouldThrowNotExistExceptionWhenPlaceDoesNotExist() {
			// When & Then
			NotExistPlaceException nepex = assertThrows(NotExistPlaceException.class,
					() -> placeRepo.get(999, CommentMode.NONE, PhotoMode.NONE));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Add place tests */
	@Nested
	@DisplayName("Add place tests")
	class AddTests {

		@Test
		@DisplayName("Should add a new place")
		void shouldAddNewPlace() throws FunctionalException, TechnicalException {
			// Given
			Place newPlace = new PlaceUser();
			User appUser = testUser;
			newPlace.setOwner(appUser);
			newPlace.setTitle("New Place");
			newPlace.setType(EntityType.CULTURE);
			newPlace.setTimes("09:00-18:00");
			newPlace.setLatitude(45.7640);
			newPlace.setLongitude(4.8357);
			newPlace.setVisibility(true);
			newPlace.setCreationDate(new Timestamp(System.currentTimeMillis()));
			newPlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			newPlace.setComments(new ArrayList<>());
			newPlace.setPhotos(new ArrayList<>());
			// When
			Place addedPlace = placeRepo.add(newPlace);
			// Then
			assertNotNull(addedPlace);
			assertNotNull(addedPlace.getId());
			assertEquals(newPlace.getTitle(), addedPlace.getTitle());
			assertEquals(newPlace.getType(), addedPlace.getType());
		}
	}

	/** Update place tests */
	@Nested
	@DisplayName("Update place tests")
	class UpdateTests {

		@Test
		@DisplayName("Should update an existing place")
		void shouldUpdateExistingPlace() throws FunctionalException, TechnicalException {
			// Given
			Place placeToUpdate = placeRepo.get(testPlace.getId(), CommentMode.NONE, PhotoMode.NONE);
			placeToUpdate.setTitle("Updated Place");
			placeToUpdate.setType(EntityType.CULTURE);
			placeToUpdate.setTimes("09:00-18:00");
			// When
			Place updatedPlace = placeRepo.update(placeToUpdate);
			// Then
			assertNotNull(updatedPlace);
			assertEquals(placeToUpdate.getId(), updatedPlace.getId());
			assertEquals("Updated Place", updatedPlace.getTitle());
			assertEquals(EntityType.CULTURE, updatedPlace.getType());
			assertEquals("09:00-18:00", updatedPlace.getTimes());
		}

		@Test
		@DisplayName("Should throw NotExistPlaceException when place does not exist")
		void shouldThrowNotExistPlaceExceptionWhenPlaceDoesNotExist() {
			// Given
			Place nonExistentPlace = new PlaceUser();
			nonExistentPlace.setId(999);
			// When & Then
			NotExistPlaceException nepex = assertThrows(NotExistPlaceException.class,
					() -> placeRepo.update(nonExistentPlace));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Delete place tests */
	@Nested
	@DisplayName("Delete place tests")
	class DeleteTests {

		@Test
		@DisplayName("Should delete an existing place")
		void shouldDeleteExistingPlace() throws FunctionalException, TechnicalException {
			// When
			Place deletedPlace = placeRepo.delete(testPlace.getId());
			// Then
			assertNotNull(deletedPlace);
			assertEquals(testPlace.getId(), deletedPlace.getId());
		}

		@Test
		@DisplayName("Should throw NotExistPlaceException when place does not exist")
		void shouldThrowNotExistPlaceExceptionWhenPlaceDoesNotExist() {
			// When & Then
			NotExistPlaceException nepex = assertThrows(NotExistPlaceException.class, () -> placeRepo.delete(999));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Count places tests */
	@Nested
	@DisplayName("Count places tests")
	class CountTests {

		@Test
		@DisplayName("Should return the number of places for a user")
		void shouldReturnUserPlacesCount() throws TechnicalException {
			// When
			Integer count = placeRepo.count(testUser.getId());
			// Then
			assertNotNull(count);
			assertEquals(1, count);
		}
	}
}
