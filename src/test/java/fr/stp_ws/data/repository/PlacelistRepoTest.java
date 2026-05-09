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
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.PlacelistUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistPlacelistException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import jakarta.persistence.PersistenceException;

/**
 * Placelist repository tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Placelist repository tests")
class placelistRepoTest {

	private static Settings settings;
	private static Hibernate hibernate;
	private PlacelistRepo placelistRepo;
	private LoadingService loadingService;
	private User testUser;
	private Placelist testPlacelist;

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
		// Placelist repository
		placelistRepo = new PlacelistRepo(hibernate, settings);
		// Manual dependency injection
		try {
			var field = PlacelistRepo.class.getDeclaredField("loadingService");
			field.setAccessible(true);
			field.set(placelistRepo, loadingService);
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
		// Create a placelist
		testPlacelist = new PlacelistUser();
		testPlacelist.setOwner(testUser);
		testPlacelist.setTitle("Test Placelist");
		testPlacelist.setType(EntityType.EAT);
		testPlacelist.setDescription("Test Description");
		testPlacelist.setVisibility(true);
		testPlacelist.setCreationDate(new Timestamp(System.currentTimeMillis()));
		testPlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		testPlacelist.setComments(new ArrayList<>());
		testPlacelist.setPlaces(new ArrayList<>());
		// Persist test entities
		var session = hibernate.openSession();
		try {
			session.persist(testUser);
			session.persist(testPlacelist);
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
			// Delete placelists
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

	/** Get all placelists tests */
	@Nested
	@DisplayName("Get all placelists tests")
	class GetAllTests {

		@Test
		@DisplayName("Should return the user's placelist list")
		void shouldReturnUserPlacelistsList() throws FunctionalException, TechnicalException {
			// When
			List<Placelist> placelists = placelistRepo.getAll(EntityCategory.USER, true, testUser.getId(), null);
			// Then
			assertNotNull(placelists);
			assertEquals(1, placelists.size());
			assertEquals(testPlacelist.getId(), placelists.get(0).getId());
		}

		@Test
		@DisplayName("Should return the public placelist list")
		void shouldReturnPublicPlacelistsList() throws FunctionalException, TechnicalException {
			// When
			List<Placelist> placelists = placelistRepo.getAll(EntityCategory.USER, false, testUser.getId(), null);
			// Then
			assertNotNull(placelists);
			assertEquals(1, placelists.size());
			assertEquals(testPlacelist.getId(), placelists.get(0).getId());
		}

		@Test
		@DisplayName("Should return the placelist list filtered by type")
		void shouldReturnPlacelistsListFilteredByType() throws FunctionalException, TechnicalException {
			// When
			List<Placelist> placelists = placelistRepo.getAll(EntityCategory.USER, true, testUser.getId(),
					EntityType.EAT);
			// Then
			assertNotNull(placelists);
			assertEquals(1, placelists.size());
			assertEquals(testPlacelist.getId(), placelists.get(0).getId());
			assertEquals(EntityType.EAT, placelists.get(0).getType());
		}

		@Test
		@DisplayName("Should not expose user B private placelist to user A")
		void shouldNotExposeUserBPrivatePlacelistToUserA() throws FunctionalException, TechnicalException {
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
			Placelist otherPrivatePlacelist = new PlacelistUser();
			otherPrivatePlacelist.setOwner(otherUser);
			otherPrivatePlacelist.setTitle("Other Private Placelist");
			otherPrivatePlacelist.setType(EntityType.EAT);
			otherPrivatePlacelist.setDescription("Other Description");
			otherPrivatePlacelist.setVisibility(false);
			otherPrivatePlacelist.setCreationDate(new Timestamp(System.currentTimeMillis()));
			otherPrivatePlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			otherPrivatePlacelist.setComments(new ArrayList<>());
			otherPrivatePlacelist.setPlaces(new ArrayList<>());
			var session = hibernate.openSession();
			try {
				session.persist(otherUser);
				session.persist(otherPrivatePlacelist);
				session.flush();
				hibernate.commit(session);
			} catch (PersistenceException pex) {
				hibernate.rollback(session);
				throw new RuntimeException("Error during additional test data persistence");
			}
			// When
			List<Placelist> placelistsVisibleToUserA = placelistRepo.getAll(EntityCategory.USER, false,
					testUser.getId(), null);
			// Then
			assertNotNull(placelistsVisibleToUserA);
			assertFalse(placelistsVisibleToUserA.stream().anyMatch(
					placelist -> placelist.getId() != null && placelist.getId().equals(otherPrivatePlacelist.getId())));
		}
	}

	/** Get placelist by id tests */
	@Nested
	@DisplayName("Get placelist by id tests")
	class GetOneTests {

		@Test
		@DisplayName("Should return an existing placelist")
		void shouldReturnExistingPlacelist() throws FunctionalException, TechnicalException {
			// When
			Placelist placelist = placelistRepo.get(testPlacelist.getId(), PlacelistMode.WITHOUT_PLACES,
					CommentMode.NONE);
			// Then
			assertNotNull(placelist);
			assertEquals(testPlacelist.getId(), placelist.getId());
			assertEquals(testPlacelist.getTitle(), placelist.getTitle());
			assertEquals(testPlacelist.getType(), placelist.getType());
		}

		@Test
		@DisplayName("Should throw NotExistPlacelistException when placelist does not exist")
		void shouldThrowNotExistPlacelistExceptionWhenPlacelistDoesNotExist() {
			// When & Then
			NotExistPlacelistException nepex = assertThrows(NotExistPlacelistException.class,
					() -> placelistRepo.get(999, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Add placelist tests */
	@Nested
	@DisplayName("Add placelist tests")
	class AddTests {

		@Test
		@DisplayName("Should add a new placelist")
		void shouldAddNewPlacelist() throws FunctionalException, TechnicalException {
			// Given
			Placelist newPlacelist = new PlacelistUser();
			User appUser = testUser;
			newPlacelist.setOwner(appUser);
			newPlacelist.setTitle("New Placelist");
			newPlacelist.setType(EntityType.CULTURE);
			newPlacelist.setDescription("New Description");
			newPlacelist.setVisibility(true);
			newPlacelist.setCreationDate(new Timestamp(System.currentTimeMillis()));
			newPlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			newPlacelist.setComments(new ArrayList<>());
			newPlacelist.setPlaces(new ArrayList<>());
			// When
			Placelist addedPlacelist = placelistRepo.add(newPlacelist);
			// Then
			assertNotNull(addedPlacelist);
			assertNotNull(addedPlacelist.getId());
			assertEquals(newPlacelist.getTitle(), addedPlacelist.getTitle());
			assertEquals(newPlacelist.getType(), addedPlacelist.getType());
		}
	}

	/** Update placelist tests */
	@Nested
	@DisplayName("Update placelist tests")
	class UpdateTests {

		@Test
		@DisplayName("Should update an existing placelist")
		void shouldUpdateExistingPlacelist() throws FunctionalException, TechnicalException {
			// Given
			Placelist placelistToUpdate = placelistRepo.get(testPlacelist.getId(), PlacelistMode.WITHOUT_PLACES,
					CommentMode.NONE);
			placelistToUpdate.setTitle("Updated Placelist");
			placelistToUpdate.setType(EntityType.CULTURE);
			placelistToUpdate.setDescription("Updated Description");
			// When
			Placelist updatedPlacelist = placelistRepo.update(placelistToUpdate);
			// Then
			assertNotNull(updatedPlacelist);
			assertEquals(placelistToUpdate.getId(), updatedPlacelist.getId());
			assertEquals("Updated Placelist", updatedPlacelist.getTitle());
			assertEquals(EntityType.CULTURE, updatedPlacelist.getType());
			assertEquals("Updated Description", updatedPlacelist.getDescription());
		}

		@Test
		@DisplayName("Should throw NotExistPlacelistException when placelist does not exist")
		void shouldThrowNotExistPlacelistExceptionWhenPlacelistDoesNotExist() {
			// Given
			Placelist nonExistentPlacelist = new PlacelistUser();
			nonExistentPlacelist.setId(999);
			// When & Then
			NotExistPlacelistException nepex = assertThrows(NotExistPlacelistException.class,
					() -> placelistRepo.update(nonExistentPlacelist));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Delete placelist tests */
	@Nested
	@DisplayName("Delete placelist tests")
	class DeleteTests {

		@Test
		@DisplayName("Should delete an existing placelist")
		void shouldDeleteExistingPlacelist() throws FunctionalException, TechnicalException {
			// When
			Placelist deletedPlacelist = placelistRepo.delete(testPlacelist.getId());
			// Then
			assertNotNull(deletedPlacelist);
			assertEquals(testPlacelist.getId(), deletedPlacelist.getId());
		}

		@Test
		@DisplayName("Should throw NotExistPlacelistException when placelist does not exist")
		void shouldThrowNotExistPlacelistExceptionWhenPlacelistDoesNotExist() {
			// When & Then
			NotExistPlacelistException nepex = assertThrows(NotExistPlacelistException.class,
					() -> placelistRepo.delete(999));
			assertNotNull(nepex.getMessage());
		}
	}

	/** Count placelists tests */
	@Nested
	@DisplayName("Count placelists tests")
	class CountTests {

		@Test
		@DisplayName("Should return the number of placelists for a user")
		void shouldReturnUserPlacelistsCount() throws TechnicalException {
			// When
			Integer count = placelistRepo.count(testUser.getId());
			// Then
			assertNotNull(count);
			assertEquals(1, count);
		}

		@Test
		@DisplayName("Should return zero places in placelist when none are associated")
		void shouldReturnZeroPlacesInPlacelistWhenEmpty() throws TechnicalException {
			// When
			Integer placeCount = placelistRepo.countPlacesIn(testPlacelist.getId());
			// Then
			assertNotNull(placeCount);
			assertEquals(0, placeCount);
		}
	}
}
