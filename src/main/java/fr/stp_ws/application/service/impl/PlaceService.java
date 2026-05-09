package fr.stp_ws.application.service.impl;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.config.ILimitsProvider;
import fr.stp_ws.application.service.inter.IPlaceService;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyCommentedPlaceException;
import fr.stp_ws.domain.exception.MaxPhotoException;
import fr.stp_ws.domain.exception.MaxPlaceException;
import fr.stp_ws.domain.exception.RestrictedAccessException;
import jakarta.inject.Inject;

/**
 * Place service implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class PlaceService implements IPlaceService {

	private static final Logger LOGGER = LogManager.getLogger(PlaceService.class);
	private final int userMaxPlaces;
	private final int touristMaxPlaces;
	private final int userMaxPhotosPerPlace;
	private final int touristMaxPhotosPerPlace;

	/**
	 * Constructor
	 *
	 * @param limitsProvider
	 */
	@Inject
	public PlaceService(ILimitsProvider limitsProvider) {
		// Loading user limits
		userMaxPlaces = limitsProvider.getUserMaxPlaces();
		touristMaxPlaces = limitsProvider.getTouristMaxPlaces();
		userMaxPhotosPerPlace = limitsProvider.getUserMaxPhotosPerPlace();
		touristMaxPhotosPerPlace = limitsProvider.getTouristMaxPhotosPerPlace();
	}

	/**
	 * Check if the user is the owner of the place
	 *
	 * @param user
	 * @param place
	 * @return Boolean
	 */
	@Override
	public Boolean isOwner(User user, Place place) {
		LOGGER.debug("Checking place ownership");
		return Objects.equals(place.getOwner().getId(), user.getId());
	}

	/**
	 * Check if the place is visible
	 *
	 * @param place
	 * @return Boolean
	 */
	@Override
	public Boolean isVisible(Place place) {
		LOGGER.debug("Checking place visibility");
		return place.getVisibility();
	}

	/**
	 * Check if the user can get the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canGet(User user, Place place) throws RestrictedAccessException {
		LOGGER.debug("Checking place access");
		// Check if the user is the owner of the place or if the place is visible
		if (!isOwner(user, place) && !isVisible(place)) {
			throw new RestrictedAccessException("You do not have access to this place");
		}
	}

	/**
	 * Check if the user can add the place
	 *
	 * @param user
	 * @param place
	 * @param currentPlaceCount
	 * @throws MaxPlaceException
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canAdd(User user, Place place, Integer currentPlaceCount)
			throws MaxPlaceException, RestrictedAccessException {
		LOGGER.debug("Checking place creation permission");
		// Check if the user has reached the maximum number of places
		int maxPlaces = user.getTourist() ? touristMaxPlaces : userMaxPlaces;
		if (currentPlaceCount >= maxPlaces) {
			throw new MaxPlaceException("You have reached the maximum number of places (" + maxPlaces + ")");
		}
		// Check if the user is the owner of the place
		if (!isOwner(user, place)) {
			throw new RestrictedAccessException("Only owner can add this place");
		}
	}

	/**
	 * Check if the user can update the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canUpdate(User user, Place place) throws RestrictedAccessException {
		LOGGER.debug("Checking place update permission");
		// Check if the user is the owner of the place
		if (!isOwner(user, place)) {
			throw new RestrictedAccessException("Only owner can update this place");
		}
	}

	/**
	 * Check if the user can delete the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canDelete(User user, Place place) throws RestrictedAccessException {
		LOGGER.debug("Checking place deletion permission");
		// Check if the user is the owner of the place
		if (!isOwner(user, place)) {
			throw new RestrictedAccessException("Only owner can delete this place");
		}
	}

	/**
	 * Check if the user can add the comment to the place
	 *
	 * @param user
	 * @param place
	 * @param hasCommentedPlace
	 * @throws AlreadyCommentedPlaceException
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canAddComment(User user, Place place, Boolean hasCommentedPlace)
			throws AlreadyCommentedPlaceException, RestrictedAccessException {
		LOGGER.debug("Checking comment creation permission");
		// Check if the user is the owner of the place or if the place is visible
		if (!isOwner(user, place) && !isVisible(place)) {
			throw new RestrictedAccessException("You do not have access to this place");
		}
		// Check if the user has already commented the place
		if (hasCommentedPlace) {
			throw new AlreadyCommentedPlaceException("You have already commented this place");
		}
	}

	/**
	 * Check if the user can delete the comment from the place
	 *
	 * @param user
	 * @param place
	 * @param comment
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canDeleteComment(User user, Place place, Comment comment) throws RestrictedAccessException {
		LOGGER.debug("Checking comment deletion permission");
		// Check if the user is the owner of the place or if the place is visible
		if (!isOwner(user, place) && !isVisible(place)) {
			throw new RestrictedAccessException("You do not have access to this place");
		}
		// Check if the user is the owner of the comment
		if (!comment.getOwner().equals(user)) {
			throw new RestrictedAccessException("You cannot delete this comment");
		}
	}

	/**
	 * Check if the user can add the photo to the place
	 *
	 * @param user
	 * @param place
	 * @param currentPhotoCount
	 * @throws MaxPhotoException
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canAddPhoto(User user, Place place, Integer currentPhotoCount)
			throws MaxPhotoException, RestrictedAccessException {
		LOGGER.debug("Checking photo creation permission");
		// Check if the user is the owner of the place
		if (!isOwner(user, place)) {
			throw new RestrictedAccessException("Only owner can add the photo to this place");
		}
		// Check if the place has reached the maximum number of photos
		int maxPhotos = user.getTourist() ? touristMaxPhotosPerPlace : userMaxPhotosPerPlace;
		if (currentPhotoCount >= maxPhotos) {
			throw new MaxPhotoException("This place has reached the maximum number of photos (" + maxPhotos + ")");
		}
	}

	/**
	 * Check if the user can delete the photo from the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canDeletePhoto(User user, Place place) throws RestrictedAccessException {
		LOGGER.debug("Checking photo deletion permission");
		// Check if the user is the owner of the place
		if (!isOwner(user, place)) {
			throw new RestrictedAccessException("Only owner can delete the photo from this place");
		}
	}
}
