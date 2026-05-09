package fr.stp_ws.data.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import fr.stp_ws.application.repository.IPlacelistRepo;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.AlreadyAssociatedException;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotAssociatedException;
import fr.stp_ws.domain.exception.NotExistPlaceException;
import fr.stp_ws.domain.exception.NotExistPlacelistException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.PersistenceException;

/**
 * Placelist repository implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class PlacelistRepo implements IPlacelistRepo {

	private static final Logger LOGGER = LogManager.getLogger(PlacelistRepo.class);
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
	private final String countPlacesIn;

	/**
	 * Constructor
	 *
	 * @param hibernate
	 * @param settings
	 */
	@Inject
	public PlacelistRepo(Hibernate hibernate, Settings settings) {
		this.hibernate = hibernate;
		// Load SQL requests from settings
		getUserAllMine = settings.getString("sql.pll.user.get.all.mine");
		getUserAllMineByType = settings.getString("sql.pll.user.get.all.mine.by.type");
		getUserAllPublic = settings.getString("sql.pll.user.get.all.public");
		getUserAllPublicByType = settings.getString("sql.pll.user.get.all.public.by.type");
		getTouristAllMine = settings.getString("sql.pll.tourist.get.all.mine");
		getTouristAllMineByType = settings.getString("sql.pll.tourist.get.all.mine.by.type");
		getTouristAllPublic = settings.getString("sql.pll.tourist.get.all.public");
		getTouristAllPublicByType = settings.getString("sql.pll.tourist.get.all.public.by.type");
		count = settings.getString("sql.pll.count");
		countPlacesIn = settings.getString("sql.pll.count.places.in");
	}

	/* Placelist - Get */

	/**
	 * Get all placelists
	 *
	 * @param category
	 * @param mine
	 * @param owner
	 * @param type
	 * @return List<Placelist>
	 * @throws TechnicalException
	 */
	@Override
	public List<Placelist> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type)
			throws TechnicalException {
		List<Placelist> placelists = new ArrayList<>();
		Session session = hibernate.openSession();
		try {
			// Determine SQL request to use
			Query<Placelist> query;
			if (category == EntityCategory.USER) {
				if (mine) {
					if (type != null && type != EntityType.ALL) {
						query = session.createQuery(getUserAllMineByType, Placelist.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getUserAllMine, Placelist.class);
					}
				} else {
					if (type != null && type != EntityType.ALL) {
						query = session.createQuery(getUserAllPublicByType, Placelist.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getUserAllPublic, Placelist.class);
					}
				}
			} else {
				if (mine) {
					if (type != null && type != EntityType.ALL) {
						query = session.createQuery(getTouristAllMineByType, Placelist.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getTouristAllMine, Placelist.class);
					}
				} else {
					if (type != null && type != EntityType.ALL) {
						query = session.createQuery(getTouristAllPublicByType, Placelist.class);
						query.setParameter("type", type);
					} else {
						query = session.createQuery(getTouristAllPublic, Placelist.class);
					}
				}
			}
			query.setParameter("owner", owner);
			// Retrieve placelists
			List<Placelist> placelistsResult = query.getResultList();
			// Load associations for each placelist
			for (Placelist placelist : placelistsResult) {
				loadingService.loadPlacelistAssociations(placelist, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
			}
			// Add placelists
			placelists.addAll(placelistsResult);
			// Sort placelists
			placelists.sort(Comparator.comparing(Placelist::getRating).reversed());
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Placelists retrieved successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving placelists : {}", pex.getMessage());
			throw new TechnicalException("Unable to get placelists");
		}
		return placelists;
	}

	/**
	 * Get a placelist
	 *
	 * @param placelistId
	 * @param placelistMode
	 * @param commentMode
	 * @return Placelist
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Placelist get(Integer placelistID, PlacelistMode placelistMode, CommentMode commentMode)
			throws FunctionalException, TechnicalException {
		Placelist placelist = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve placelist
			placelist = session.find(Placelist.class, placelistID);
			if (placelist == null) {
				throw new NotExistPlacelistException("Placelist does not exist");
			}
			// Load associations of placelist
			loadingService.loadPlacelistAssociations(placelist, placelistMode, commentMode);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Placelist retrieved successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving placelist : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving placelist : {}", pex.getMessage());
			throw new TechnicalException("Unable to get placelist");
		}
		return placelist;
	}

	/* Placelist - Add / Update / Delete */

	/**
	 * Add a placelist
	 *
	 * @param placelist
	 * @return Placelist
	 * @throws TechnicalException
	 */
	@Override
	public Placelist add(Placelist placelist) throws TechnicalException {
		Placelist dataPlacelist = placelist;
		Session session = hibernate.openSession();
		try {
			// Save placelist
			session.persist(dataPlacelist);
			// Load associations of placelist
			loadingService.loadPlacelistAssociations(dataPlacelist, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Placelist added successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding placelist : {}", pex.getMessage());
			throw new TechnicalException("Unable to add placelist");
		}
		return dataPlacelist;
	}

	/**
	 * Update a placelist
	 *
	 * @param placelist
	 * @return Placelist
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Placelist update(Placelist placelist) throws FunctionalException, TechnicalException {
		Placelist existingPlacelist = null;
		Placelist dataPlacelist = placelist;
		Session session = hibernate.openSession();
		try {
			// Retrieve placelist
			existingPlacelist = session.find(Placelist.class, dataPlacelist.getId());
			if (existingPlacelist == null) {
				throw new NotExistPlacelistException("Placelist does not exist");
			}
			// Modify placelist
			existingPlacelist.setType(dataPlacelist.getType());
			existingPlacelist.setTitle(dataPlacelist.getTitle());
			existingPlacelist.setDescription(dataPlacelist.getDescription());
			existingPlacelist.setVisibility(dataPlacelist.getVisibility());
			existingPlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			// Update placelist
			session.merge(existingPlacelist);
			// Load associations of placelist
			loadingService.loadPlacelistAssociations(existingPlacelist, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Placelist updated successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while updating placelist : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while updating placelist : {}", pex.getMessage());
			throw new TechnicalException("Unable to update placelist");
		}
		return existingPlacelist;
	}

	/**
	 * Delete a placelist
	 *
	 * @param placelistId
	 * @return Placelist
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Placelist delete(Integer placelistId) throws FunctionalException, TechnicalException {
		Placelist deletedPlacelist = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve placelist
			Placelist placelist = session.find(Placelist.class, placelistId);
			if (placelist == null) {
				throw new NotExistPlacelistException("Placelist does not exist");
			}
			// Delete placelist
			deletedPlacelist = placelist;
			session.remove(placelist);
			// Load associations of placelist
			loadingService.loadPlacelistAssociations(deletedPlacelist, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Placelist deleted successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting placelist : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting placelist : {}", pex.getMessage());
			throw new TechnicalException("Unable to delete placelist");
		}
		return deletedPlacelist;
	}

	/* Place - Add / Remove */

	/**
	 * Add a place to a placelist
	 *
	 * @param placelistId
	 * @param placeId
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Place addPlace(Integer placelistId, Integer placeId) throws FunctionalException, TechnicalException {
		Place addedPlace = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve placelist
			Placelist placelist = session.find(Placelist.class, placelistId);
			if (placelist == null) {
				throw new NotExistPlacelistException("Placelist does not exist");
			}
			// Retrieve place
			Place place = session.find(Place.class, placeId);
			if (place == null) {
				throw new NotExistPlaceException("Place does not exist");
			}
			// Check if the place is already in the placelist
			if (placelist.getPlaces().contains(place)) {
				throw new AlreadyAssociatedException("The place is already in the placelist");
			}
			// Add place in placelist
			placelist.getPlaces().add(place);
			// Save placelist
			session.merge(placelist);
			addedPlace = place;
			// Load associations of place
			loadingService.loadPlaceAssociations(addedPlace, CommentMode.NONE, PhotoMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Place added to placelist successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding place to placelist : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding place to placelist : {}", pex.getMessage());
			throw new TechnicalException("Unable to add place to placelist");
		}
		return addedPlace;
	}

	/**
	 * Remove a place from a placelist
	 *
	 * @param placelistId
	 * @param placeId
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Place removePlace(Integer placelistId, Integer placeId) throws FunctionalException, TechnicalException {
		Place removedPlace = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve placelist
			Placelist placelist = session.find(Placelist.class, placelistId);
			if (placelist == null) {
				throw new NotExistPlacelistException("Placelist does not exist");
			}
			// Retrieve place
			Place place = session.find(Place.class, placeId);
			if (place == null) {
				throw new NotExistPlaceException("Place does not exist");
			}
			// Check if the place is in the placelist
			if (!placelist.getPlaces().contains(place)) {
				throw new NotAssociatedException("The place is not in the placelist");
			}
			// Remove place from placelist
			placelist.getPlaces().remove(place);
			// Save placelist
			session.merge(placelist);
			removedPlace = place;
			// Load associations of place
			loadingService.loadPlaceAssociations(removedPlace, CommentMode.NONE, PhotoMode.NONE);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Place removed from placelist successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while removing place from placelist : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while removing place from placelist : {}", pex.getMessage());
			throw new TechnicalException("Unable to remove place from placelist");
		}
		return removedPlace;
	}

	/* Placelist - Count */

	/**
	 * Count the number of placelists for a user
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
			LOGGER.debug("Counted placelists successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while counting placelists : {}", pex.getMessage());
			throw new TechnicalException("Unable to count placelists");
		}
		return result;
	}

	/**
	 * Count the number of places already associated with a placelist
	 *
	 * @param placelistId
	 * @return Integer
	 * @throws TechnicalException
	 */
	@Override
	public Integer countPlacesIn(Integer placelistId) throws TechnicalException {
		Integer result = 0;
		Session session = hibernate.openSession();
		try {
			// Create query
			Query<Long> query = session.createQuery(countPlacesIn, Long.class);
			query.setParameter("placelistId", placelistId);
			Long countResult = query.getSingleResult();
			// Define result
			result = countResult != null ? countResult.intValue() : 0;
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Counted places in placelist successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while counting places in placelist : {}", pex.getMessage());
			throw new TechnicalException("Unable to count places in placelist");
		}
		return result;
	}
}
