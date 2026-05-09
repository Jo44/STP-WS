package fr.stp_ws.data.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Hibernate;

import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import jakarta.inject.Singleton;

/**
 * Loading service (Hibernate lazy associations)
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class LoadingService {

	private static final Logger LOGGER = LogManager.getLogger(LoadingService.class);

	/** Constructor */
	public LoadingService() {
		super();
	}

	/**
	 * Load place associations (according to the specified modes)
	 *
	 * @param place
	 * @param commentMode
	 * @param photoMode
	 */
	public void loadPlaceAssociations(Place place, CommentMode commentMode, PhotoMode photoMode) {
		if (place == null) {
			return;
		}
		LOGGER.debug("Loading place associations");
		// Load owner
		loadPlaceOwner(place);
		// Load comments
		if (commentMode != CommentMode.NONE) {
			loadPlaceComments(place);
		}
		// Load photos
		if (photoMode != PhotoMode.NONE) {
			loadPlacePhotos(place);
		}
	}

	/**
	 * Load placelist associations (according to the specified modes)
	 *
	 * @param placelist
	 * @param placelistMode
	 * @param commentMode
	 */
	public void loadPlacelistAssociations(Placelist placelist, PlacelistMode placelistMode, CommentMode commentMode) {
		if (placelist == null) {
			return;
		}
		LOGGER.debug("Loading placelist associations");
		// Load owner
		loadPlacelistOwner(placelist);
		// Load places
		if (placelistMode == PlacelistMode.WITH_PLACES) {
			loadPlacelistPlaces(placelist);
			if (placelist.getPlaces() != null && !placelist.getPlaces().isEmpty()) {
				for (Place place : placelist.getPlaces()) {
					// Load place associations
					loadPlaceAssociations(place, CommentMode.NONE, PhotoMode.NONE);
				}
			}
		}
		// Load comments
		if (commentMode != CommentMode.NONE) {
			loadPlacelistComments(placelist);
		}
	}

	/**
	 * Load comment associations
	 *
	 * @param comment
	 */
	public void loadCommentAssociations(Comment comment) {
		if (comment == null) {
			return;
		}
		LOGGER.debug("Loading comment associations");
		// Load owner
		loadCommentOwner(comment);
	}

	/**
	 * Load photo associations
	 *
	 * @param photo
	 */
	public void loadPhotoAssociations(Photo photo) {
		if (photo == null) {
			return;
		}
		LOGGER.debug("Loading photo associations");
		// Load place
		loadPhotoPlace(photo);
	}

	/**
	 * Load places from placelist
	 *
	 * @param placelist
	 */
	private void loadPlacelistPlaces(Placelist placelist) {
		if (placelist != null && !Hibernate.isInitialized(placelist.getPlaces())) {
			Hibernate.initialize(placelist.getPlaces());
		}
	}

	/**
	 * Load place owner
	 *
	 * @param place
	 */
	private void loadPlaceOwner(Place place) {
		if (place != null && !Hibernate.isInitialized(place.getOwner())) {
			Hibernate.initialize(place.getOwner());
		}
	}

	/**
	 * Load placelist owner
	 *
	 * @param placelist
	 */
	private void loadPlacelistOwner(Placelist placelist) {
		if (placelist != null && !Hibernate.isInitialized(placelist.getOwner())) {
			Hibernate.initialize(placelist.getOwner());
		}
	}

	/**
	 * Load comment owner
	 *
	 * @param comment
	 */
	private void loadCommentOwner(Comment comment) {
		if (comment != null && !Hibernate.isInitialized(comment.getOwner())) {
			Hibernate.initialize(comment.getOwner());
		}
	}

	/**
	 * Load comments from place
	 *
	 * @param place
	 */
	private void loadPlaceComments(Place place) {
		if (place != null && !Hibernate.isInitialized(place.getComments())) {
			Hibernate.initialize(place.getComments());
		}
	}

	/**
	 * Load comments from placelist
	 *
	 * @param placelist
	 */
	private void loadPlacelistComments(Placelist placelist) {
		if (placelist != null && !Hibernate.isInitialized(placelist.getComments())) {
			Hibernate.initialize(placelist.getComments());
		}
	}

	/**
	 * Load photos from place
	 *
	 * @param place
	 */
	private void loadPlacePhotos(Place place) {
		if (place != null && !Hibernate.isInitialized(place.getPhotos())) {
			Hibernate.initialize(place.getPhotos());
		}
	}

	/**
	 * Load place from photo
	 *
	 * @param photo
	 */
	private void loadPhotoPlace(Photo photo) {
		if (photo != null && !Hibernate.isInitialized(photo.getPlace())) {
			Hibernate.initialize(photo.getPlace());
		}
	}
}
