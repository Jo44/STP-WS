package fr.stp_ws.application.service.impl;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.config.ILimitsProvider;
import fr.stp_ws.application.service.inter.IPlacelistService;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.Entity;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyCommentedPlacelistException;
import fr.stp_ws.domain.exception.MaxPlaceByPlacelistException;
import fr.stp_ws.domain.exception.MaxPlacelistException;
import fr.stp_ws.domain.exception.RestrictedAccessException;
import jakarta.inject.Inject;

/**
 * Placelist service implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class PlacelistService implements IPlacelistService {

	private static final Logger LOGGER = LogManager.getLogger(PlacelistService.class);
	private final int userMaxPlacelists;
	private final int touristMaxPlacelists;
	private final int userMaxPlacesPerPlacelist;
	private final int touristMaxPlacesPerPlacelist;

	/**
	 * Constructor
	 *
	 * @param limitsProvider
	 */
	@Inject
	public PlacelistService(ILimitsProvider limitsProvider) {
		// Load user limits
		userMaxPlacelists = limitsProvider.getUserMaxPlacelists();
		touristMaxPlacelists = limitsProvider.getTouristMaxPlacelists();
		userMaxPlacesPerPlacelist = limitsProvider.getUserMaxPlacesPerPlacelist();
		touristMaxPlacesPerPlacelist = limitsProvider.getTouristMaxPlacesPerPlacelist();
	}

	/**
	 * Check if the user is the owner of the entity
	 *
	 * @param user
	 * @param entity
	 * @return Boolean
	 */
	@Override
	public Boolean isOwner(User user, Entity entity) {
		LOGGER.debug("Checking entity ownership");
		Boolean isOwner;
		// According to the type of entity
		switch (entity) {
		case Placelist placelist -> isOwner = Objects.equals(placelist.getOwner().getId(), user.getId());
		case Place place -> isOwner = Objects.equals(place.getOwner().getId(), user.getId());
		default -> isOwner = false;
		}
		return isOwner;
	}

	/**
	 * Check if the entity is visible
	 *
	 * @param entity
	 * @return Boolean
	 */
	@Override
	public Boolean isVisible(Entity entity) {
		LOGGER.debug("Checking entity visibility");
		Boolean isVisible;
		// According to the type of entity
		switch (entity) {
		case Placelist placelist -> isVisible = placelist.getVisibility();
		case Place place -> isVisible = place.getVisibility();
		default -> isVisible = false;
		}
		return isVisible;
	}

	/**
	 * Check if the user can get the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canGet(User user, Placelist placelist) throws RestrictedAccessException {
		LOGGER.debug("Checking placelist access");
		// Check if the user is the owner of the placelist or if the placelist is
		// visible
		if (!isOwner(user, placelist) && !isVisible(placelist)) {
			throw new RestrictedAccessException("You do not have access to this placelist");
		}
	}

	/**
	 * Check if the user can add the placelist
	 *
	 * @param user
	 * @param placelist
	 * @param currentPlacelistCount
	 * @throws MaxPlacelistException
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canAdd(User user, Placelist placelist, Integer currentPlacelistCount)
			throws MaxPlacelistException, RestrictedAccessException {
		LOGGER.debug("Checking placelist creation permission");
		// Check if the user has reached the maximum number of placelists
		int maxPlacelists = user.getTourist() ? touristMaxPlacelists : userMaxPlacelists;
		if (currentPlacelistCount >= maxPlacelists) {
			throw new MaxPlacelistException(
					"You have reached the maximum number of placelists (" + maxPlacelists + ")");
		}
		// Check if the user is the owner of the placelist
		if (!isOwner(user, placelist)) {
			throw new RestrictedAccessException("Only owner can add this placelist");
		}
	}

	/**
	 * Check if the user can update the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canUpdate(User user, Placelist placelist) throws RestrictedAccessException {
		LOGGER.debug("Checking placelist update permission");
		// Check if the user is the owner of the placelist
		if (!isOwner(user, placelist)) {
			throw new RestrictedAccessException("Only owner can update this placelist");
		}
	}

	/**
	 * Check if the user can delete the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canDelete(User user, Placelist placelist) throws RestrictedAccessException {
		LOGGER.debug("Checking placelist deletion permission");
		// Check if the user is the owner of the placelist
		if (!isOwner(user, placelist)) {
			throw new RestrictedAccessException("Only owner can delete this placelist");
		}
	}

	/**
	 * Check if the user can add a comment to the placelist
	 *
	 * @param user
	 * @param placelist
	 * @param hasCommentedPlacelist
	 * @throws AlreadyCommentedPlacelistException
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canAddComment(User user, Placelist placelist, Boolean hasCommentedPlacelist)
			throws AlreadyCommentedPlacelistException, RestrictedAccessException {
		LOGGER.debug("Checking comment creation permission");
		// Check if the user is the owner of the placelist or if the placelist is
		// visible
		if (!isOwner(user, placelist) && !isVisible(placelist)) {
			throw new RestrictedAccessException("You do not have access to this placelist");
		}
		// Check if the user has already commented the placelist
		if (hasCommentedPlacelist) {
			throw new AlreadyCommentedPlacelistException("You have already commented this placelist");
		}
	}

	/**
	 * Check if the user can delete the comment from the placelist
	 *
	 * @param user
	 * @param placelist
	 * @param comment
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canDeleteComment(User user, Placelist placelist, Comment comment) throws RestrictedAccessException {
		LOGGER.debug("Checking comment deletion permission");
		// Check if the user is the owner of the placelist or if the placelist is
		// visible
		if (!isOwner(user, placelist) && !isVisible(placelist)) {
			throw new RestrictedAccessException("You do not have access to this placelist");
		}
		// Check if the user is the owner of the comment
		if (!comment.getOwner().equals(user)) {
			throw new RestrictedAccessException("You cannot delete this comment");
		}
	}

	/**
	 * Check if the user can add the place to the placelist
	 *
	 * @param user
	 * @param placelist
	 * @param place
	 * @param currentPlaceCount
	 * @throws MaxPlaceByPlacelistException
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canAddPlace(User user, Placelist placelist, Place place, Integer currentPlaceCount)
			throws MaxPlaceByPlacelistException, RestrictedAccessException {
		LOGGER.debug("Checking place association permission");
		// Check if the user is the owner of the placelist
		if (!isOwner(user, placelist)) {
			throw new RestrictedAccessException("Only owner can add the place to this placelist");
		}
		// Check if the placelist has reached the maximum number of places
		int maxPlaces = user.getTourist() ? touristMaxPlacesPerPlacelist : userMaxPlacesPerPlacelist;
		if (currentPlaceCount >= maxPlaces) {
			throw new MaxPlaceByPlacelistException(
					"This placelist has reached the maximum number of places (" + maxPlaces + ")");
		}
		// Check if the user is the owner of the place or if the place is visible
		if (!isOwner(user, place) && !isVisible(place)) {
			throw new RestrictedAccessException("You cannot add a private place if you are not the owner");
		}
	}

	/**
	 * Check if the user can remove the place from the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	@Override
	public void canRemovePlace(User user, Placelist placelist) throws RestrictedAccessException {
		LOGGER.debug("Checking place dissociation permission");
		// Check if the user is the owner of the placelist
		if (!isOwner(user, placelist)) {
			throw new RestrictedAccessException("Only owner can remove the place from this placelist");
		}
	}
}
