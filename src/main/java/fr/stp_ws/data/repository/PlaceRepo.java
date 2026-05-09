package fr.stp_ws.data.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import fr.stp_ws.application.repository.IPlaceRepo;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistPlaceException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.PersistenceException;

/**
 * Place repository implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class PlaceRepo implements IPlaceRepo {

	private static final Logger LOGGER = LogManager.getLogger(PlaceRepo.class);
	@Inject
	private LoadingService loadingService;
	private final Hibernate hibernate;
	private final String getUserAllMine;
	private final String getUserAllMineByType;
	private final String getUserAllPublic;
	private final String getUserAllPublicByType;
	private final String getTouristAllMine;
	private final String getTouristAllMineByType;
	private final String getTouristAllPublic;
	private final String getTouristAllPublicByType;
	private final String count;

	/**
	 * Constructor
	 *
	 * @param hibernate
	 * @param settings
	 */
	@Inject
	public PlaceRepo(Hibernate hibernate, Settings settings) {
		super();
		this.hibernate = hibernate;
		// Load SQL requests from settings
		getUserAllMine = settings.getString("sql.pl.user.get.all.mine");
		getUserAllMineByType = settings.getString("sql.pl.user.get.all.mine.by.type");
		getUserAllPublic = settings.getString("sql.pl.user.get.all.public");
		getUserAllPublicByType = settings.getString("sql.pl.user.get.all.public.by.type");
		getTouristAllMine = settings.getString("sql.pl.tourist.get.all.mine");
		getTouristAllMineByType = settings.getString("sql.pl.tourist.get.all.mine.by.type");
		getTouristAllPublic = settings.getString("sql.pl.tourist.get.all.public");
		getTouristAllPublicByType = settings.getString("sql.pl.tourist.get.all.public.by.type");
		count = settings.getString("sql.pl.count");
	}

	/* Place - Get */

	/**
	 * Get all places
	 *
	 * @param category
	 * @param mine
	 * @param owner
	 * @param type
	 * @param fromLat
	 * @param toLat
	 * @param fromLong
	 * @param toLong
	 * @return List<Place>
	 * @throws TechnicalException
	 */
	@Override
	public List<Place> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type, Double fromLat,
			Double toLat, Double fromLong, Double toLong) throws TechnicalException {
		List<Place> places = new ArrayList<>();
		Session session = hibernate.openSession();
		try {
			// Determine SQL request to use
			Query<Place> query;
			if (category == EntityCategory.USER) {
				if (mine) {
					if (type != null && type != EntityType.ALL) {
						query = session.createQuery(getUserAllMineByType, Place.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getUserAllMine, Place.class);
					}
				} else {
					if (type != null && type != EntityType.ALL) {
						query = session.createQuery(getUserAllPublicByType, Place.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getUserAllPublic, Place.class);
					}
				}
			} else {
				if (mine) {
					if (type != null && type != EntityType.ALL) {
					query = session.createQuery(getTouristAllMineByType, Place.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getTouristAllMine, Place.class);
					}
				} else {
					if (type != null && type != EntityType.ALL) {
						query = session.createQuery(getTouristAllPublicByType, Place.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getTouristAllPublic, Place.class);
					}
				}
			}
			query.setParameter("owner", owner);
			query.setParameter("fromLat", fromLat);
			query.setParameter("toLat", toLat);
			query.setParameter("fromLong", fromLong);
			query.setParameter("toLong", toLong);
			// Retrieve places
			List<Place> placesResult = query.getResultList();
			// Load associations for each place
			for (Place place : placesResult) {
				loadingService.loadPlaceAssociations(place, CommentMode.NONE, PhotoMode.NONE);
			}
			// Add places
			places.addAll(placesResult);
			// Sort places
			places.sort(Comparator.comparing(Place::getRating).reversed());
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Places retrieved successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving places : {}", pex.getMessage());
			throw new TechnicalException("Unable to get places");
		}
		return places;
	}

	/**
	 * Get a place
	 *
	 * @param placeId
	 * @param commentMode
	 * @param photoMode
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Place get(Integer placeId, CommentMode commentMode, PhotoMode photoMode)
			throws FunctionalException, TechnicalException {
		Place place = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve place
			place = session.find(Place.class, placeId);
			if (place == null) {
				throw new NotExistPlaceException("Place does not exist");
			}
			// Load associations of place
			loadingService.loadPlaceAssociations(place, commentMode, photoMode);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Place retrieved successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving place : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving place : {}", pex.getMessage());
			throw new TechnicalException("Unable to get place");
		}
		return place;
	}

	/* Place - Add / Update / Delete */

	/**
	 * Add a place
	 *
	 * @param place
	 * @return Place
	 * @throws TechnicalException
	 */
	@Override
	public Place add(Place place) throws TechnicalException {
		Place dataPlace = place;
		Session session = hibernate.openSession();
		try {
			// Save place
			session.persist(dataPlace);
			// Load associations of place
			loadingService.loadPlaceAssociations(dataPlace, CommentMode.NONE, PhotoMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Place added successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding place : {}", pex.getMessage());
			throw new TechnicalException("Unable to add place");
		}
		return dataPlace;
	}

	/**
	 * Update a place
	 *
	 * @param place
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Place update(Place place) throws FunctionalException, TechnicalException {
		Place existingPlace = null;
		Place dataPlace = place;
		Session session = hibernate.openSession();
		try {
			// Retrieve place
			existingPlace = session.find(Place.class, dataPlace.getId());
			if (existingPlace == null) {
				throw new NotExistPlaceException("Place does not exist");
			}
			// Modify place
			existingPlace.setType(dataPlace.getType());
			existingPlace.setTimes(dataPlace.getTimes());
			existingPlace.setLatitude(dataPlace.getLatitude());
			existingPlace.setLongitude(dataPlace.getLongitude());
			existingPlace.setTitle(dataPlace.getTitle());
			existingPlace.setDescription(dataPlace.getDescription());
			existingPlace.setVisibility(dataPlace.getVisibility());
			existingPlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// Update place
			session.merge(existingPlace);
			// Load associations of place
			loadingService.loadPlaceAssociations(existingPlace, CommentMode.NONE, PhotoMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Place updated successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while updating place : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while updating place : {}", pex.getMessage());
			throw new TechnicalException("Unable to update place");
		}
		return existingPlace;
	}

	/**
	 * Delete a place
	 *
	 * @param placeId
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Place delete(Integer placeId) throws FunctionalException, TechnicalException {
		Place deletedPlace = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve place
			Place place = session.find(Place.class, placeId);
			if (place == null) {
				throw new NotExistPlaceException("Place does not exist");
			}
			// Delete place
			deletedPlace = place;
			session.remove(place);
			// Load associations of place
			loadingService.loadPlaceAssociations(deletedPlace, CommentMode.NONE, PhotoMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Place deleted successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting place : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting place : {}", pex.getMessage());
			throw new TechnicalException("Unable to delete place");
		}
		return deletedPlace;
	}

	/* Place - Count */

	/**
	 * Count the number of places for a user
	 *
	 * @param userId
	 * @return Integer
	 * @throws TechnicalException
	 */
	@Override
	public Integer count(Integer userId) throws TechnicalException {
		Integer result = 0;
		Session session = hibernate.openSession();
		try {
			// Create query
			Query<Long> query = session.createQuery(count, Long.class);
			query.setParameter("owner", userId);
			Long countResult = query.getSingleResult();
			// Define result
			result = countResult != null ? countResult.intValue() : 0;
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Counted places successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while counting places : {}", pex.getMessage());
			throw new TechnicalException("Unable to count places");
		}
		return result;
	}
}
