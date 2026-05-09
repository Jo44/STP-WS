package fr.stp_ws.data.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import fr.stp_ws.application.repository.IPhotoRepo;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistPhotoException;
import fr.stp_ws.domain.exception.TechnicalException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.PersistenceException;

/**
 * Photo repository implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class PhotoRepo implements IPhotoRepo {

	private static final Logger LOGGER = LogManager.getLogger(PhotoRepo.class);
	@Inject
	private LoadingService loadingService;
	private final Hibernate hibernate;
	private final String getAll;
	private final String count;

	/**
	 * Constructor
	 *
	 * @param hibernate
	 * @param settings
	 */
	@Inject
	public PhotoRepo(Hibernate hibernate, Settings settings) {
		this.hibernate = hibernate;
		// Load SQL requests from settings
		getAll = settings.getString("sql.photo.get.all");
		count = settings.getString("sql.photo.count");
	}

	/* Photo - Get */

	/**
	 * Get all photos
	 *
	 * @param placeId
	 * @param owner
	 * @return List<Photo>
	 * @throws TechnicalException
	 */
	@Override
	public List<Photo> getAll(Integer placeId, Integer owner) throws TechnicalException {
		List<Photo> photos = new ArrayList<>();
		Session session = hibernate.openSession();
		try {
			// Create query
			Query<Photo> query = session.createQuery(getAll, Photo.class);
			query.setParameter("id", placeId);
			query.setParameter("owner", owner);
			// Retrieve photos
			List<Photo> photosResult = query.getResultList();
			// Load associations for each photo
			for (Photo photo : photosResult) {
				loadingService.loadPhotoAssociations(photo);
			}
			// Add photos
			photos.addAll(photosResult);
			// Sort photos
			photos.sort(Comparator.comparing(Photo::getUploadDate).reversed());
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Photos retrieved successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving photos : {}", pex.getMessage());
			throw new TechnicalException("Unable to get photos");
		}
		return photos;
	}

	/**
	 * Get a photo
	 *
	 * @param photoId
	 * @return Photo
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Photo get(Integer photoId) throws FunctionalException, TechnicalException {
		Photo photo = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve photo
			photo = session.find(Photo.class, photoId);
			if (photo == null) {
				throw new NotExistPhotoException("Photo does not exist");
			}
			// Load associations of photo
			loadingService.loadPhotoAssociations(photo);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Photo retrieved successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving photo : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving photo : {}", pex.getMessage());
			throw new TechnicalException("Unable to get photo");
		}
		return photo;
	}

	/* Photo - Add / Delete */

	/**
	 * Add a photo
	 *
	 * @param photo
	 * @return Photo
	 * @throws TechnicalException
	 */
	@Override
	public Photo add(Photo photo) throws TechnicalException {
		Photo dataPhoto = photo;
		Session session = hibernate.openSession();
		try {
			// Save photo
			session.persist(dataPhoto);
			// Load associations of photo
			loadingService.loadPhotoAssociations(dataPhoto);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Photo added successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding photo : {}", pex.getMessage());
			throw new TechnicalException("Unable to add photo");
		}
		return dataPhoto;
	}

	/**
	 * Delete a photo
	 *
	 * @param photoId
	 * @return Photo
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Photo delete(Integer photoId) throws FunctionalException, TechnicalException {
		Photo deletedPhoto = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve photo
			Photo photo = session.find(Photo.class, photoId);
			if (photo == null) {
				throw new NotExistPhotoException("Photo does not exist");
			}
			// Delete photo
			deletedPhoto = photo;
			session.remove(photo);
			// Load associations of photo
			loadingService.loadPhotoAssociations(deletedPhoto);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Photo deleted successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting photo : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting photo : {}", pex.getMessage());
			throw new TechnicalException("Unable to delete photo");
		}
		return deletedPhoto;
	}

	/* Photo - Count */

	/**
	 * Count the number of photos for a place
	 *
	 * @param placeId
	 * @return Integer
	 * @throws TechnicalException
	 */
	@Override
	public Integer count(Integer placeId) throws TechnicalException {
		Integer result = 0;
		Session session = hibernate.openSession();
		try {
			// Create query
			Query<Long> query = session.createQuery(count, Long.class);
			query.setParameter("id", placeId);
			Long countResult = query.getSingleResult();
			// Define result
			result = countResult != null ? countResult.intValue() : 0;
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Counted photos successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while counting photos : {}", pex.getMessage());
			throw new TechnicalException("Unable to count photos");
		}
		return result;
	}
}
