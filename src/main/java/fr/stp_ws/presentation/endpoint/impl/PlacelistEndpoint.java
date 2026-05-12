package fr.stp_ws.presentation.endpoint.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.usecase.inter.IPlacelistUC;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import fr.stp_ws.presentation.endpoint.inter.IPlacelistEndpoint;
import fr.stp_ws.presentation.exception.InvalidRequestException;
import fr.stp_ws.presentation.security.SecurityContextUser;
import fr.stp_ws.presentation.validator.RequestValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

/**
 * Placelist endpoints implementation
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
@Singleton
@Path("/placelist")
public class PlacelistEndpoint extends AbstractEndpoint implements IPlacelistEndpoint {

	private static final Logger LOGGER = LogManager.getLogger(PlacelistEndpoint.class);
	@Context
	private SecurityContext securityContext;
	private final IPlacelistUC placelistUC;

	/** Constructor */
	@Inject
	public PlacelistEndpoint(IPlacelistUC placelistUC) {
		super();
		this.placelistUC = placelistUC;
	}

	/* Placelist - [GET] / [POST] / [PUT] / [DELETE] */

	/**
	 * Get all placelists (according to parameters)
	 *
	 * @param category
	 * @param mine
	 * @param type
	 * @return List<PlacelistDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/list/{category}/{mine}/{type}
	@Override
	public List<PlacelistDTO> getPlacelists(String category, Boolean mine, String type)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [GET] Placelists - /placelist/list/" + category + "/" + String.valueOf(mine)
				+ "/" + type);
		return execute(LOGGER, () -> {
			List<PlacelistDTO> placelists = new ArrayList<>();
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Retrieve placelists category and type
			EntityCategory foundCategory = EntityCategory.fromValue(category);
			EntityType foundType = EntityType.fromValue(type);
			// Based on the category
			switch (foundCategory) {
			// Retrieve all placelists (User and Tourist)
			case ALL -> {
				List<PlacelistDTO> placelistsUser = placelistUC.getAll(EntityCategory.USER, mine, userId, foundType);
				List<PlacelistDTO> placelistsTourist = placelistUC.getAll(EntityCategory.TOURIST, mine, userId,
						foundType);
				placelists.addAll(placelistsUser);
				placelists.addAll(placelistsTourist);
			}
			// Retrieve all placelists (User or Tourist)
			case USER, TOURIST -> placelists = placelistUC.getAll(foundCategory, mine, userId, foundType);
			}
			// Return DTOs
			return placelists;
		});
	}

	/**
	 * Get the placelist
	 *
	 * @param validator
	 * @param placelistId
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/{placelistId}
	@Override
	public PlacelistDTO getPlacelist(RequestValidator validator, Integer placelistId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [GET] Placelist - /placelist/" + String.valueOf(placelistId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Retrieve placelist
			return placelistUC.get(placelistId, userId, PlacelistMode.WITH_PLACES, CommentMode.MAX_3);
		});
	}

	/**
	 * Count owner placelists
	 *
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/count
	@Override
	public CountDTO countOwnerPlacelists() throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [GET] Placelists count - /placelist/count");
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Count owner placelists
			return placelistUC.countOwnerPlacelists(userId);
		});
	}

	/**
	 * Add the placelist
	 *
	 * @param validator
	 * @param placelistDTO
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist
	@Override
	public PlacelistDTO addPlacelist(RequestValidator validator, PlacelistDTO placelistDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [POST] Placelist - /placelist/");
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist
			if (!validator.checkPlacelist(placelistDTO)) {
				throw new InvalidRequestException("Invalid placelist");
			}
			// Add placelist
			return placelistUC.add(placelistDTO, userId);
		});
	}

	/**
	 * Update the placelist
	 *
	 * @param validator
	 * @param placelistDTO
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist
	@Override
	public PlacelistDTO updatePlacelist(RequestValidator validator, PlacelistDTO placelistDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [PUT] Placelist - /placelist");
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist
			if (!validator.checkPlacelist(placelistDTO)) {
				throw new InvalidRequestException("Invalid placelist");
			}
			// Update placelist
			return placelistUC.update(placelistDTO, userId);
		});
	}

	/**
	 * Delete the placelist
	 *
	 * @param validator
	 * @param placelistId
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/{placelistId}
	@Override
	public PlacelistDTO deletePlacelist(RequestValidator validator, Integer placelistId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [DELETE] Placelist - /placelist/" + String.valueOf(placelistId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Delete placelist
			return placelistUC.delete(placelistId, userId);
		});
	}

	/* Comment - [GET] / [POST] / [DELETE] */

	/**
	 * Get all comments
	 *
	 * @param validator
	 * @param placelistId
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/comments/list/{placelistId}
	@Override
	public List<CommentDTO> getComments(RequestValidator validator, Integer placelistId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [GET] Comments - /placelist/comments/list/" + String.valueOf(placelistId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Retrieve all comments
			// Return DTOs
			return placelistUC.getComments(placelistId, userId);
		});
	}

	/**
	 * Count owner comment
	 *
	 * @param validator
	 * @param placelistId
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/comments/count/{placelistId}
	@Override
	public CountDTO countOwnerComment(RequestValidator validator, Integer placelistId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [GET] Comments count - /placelist/comments/count/"
				+ String.valueOf(placelistId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Count owner comment
			return placelistUC.countOwnerComment(placelistId, userId);
		});
	}

	/**
	 * Add the comment
	 *
	 * @param validator
	 * @param placelistId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/comment/{placelistId}
	@Override
	public CommentDTO addComment(RequestValidator validator, Integer placelistId, CommentDTO commentDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [POST] Comment - /placelist/comment/" + String.valueOf(placelistId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Check comment
			if (!validator.checkComment(commentDTO)) {
				throw new InvalidRequestException("Invalid comment");
			}
			// Add comment
			return placelistUC.addComment(placelistId, commentDTO, userId);
		});
	}

	/**
	 * Delete the comment
	 *
	 * @param validator
	 * @param placelistId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/comment/{placelistId}
	@Override
	public CommentDTO deleteComment(RequestValidator validator, Integer placelistId, CommentDTO commentDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [DELETE] Comment - /placelist/comment/" + String.valueOf(placelistId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Check comment
			if (!validator.checkComment(commentDTO)) {
				throw new InvalidRequestException("Invalid comment");
			}
			// Delete comment
			return placelistUC.deleteComment(placelistId, commentDTO.getId(), userId);
		});
	}

	/* Place - [GET] / [POST] / [DELETE] */

	/**
	 * Count all places in the placelist
	 *
	 * @param validator
	 * @param placelistId
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/places/count/{placelistId}
	@Override
	public CountDTO countPlacesInPlacelist(RequestValidator validator, Integer placelistId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [GET] PlacesInPlacelist count - /placelist/places/count/"
				+ String.valueOf(placelistId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Count places in placelist
			return placelistUC.countPlacesInPlacelist(placelistId, userId);
		});
	}

	/**
	 * Add the place
	 *
	 * @param validator
	 * @param placelistId
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/{placelistId}/place/{placeId}
	@Override
	public PlaceDTO addPlaceToPlacelist(RequestValidator validator, Integer placelistId, Integer placeId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [POST] PlaceToPlacelist - /placelist/" + String.valueOf(placelistId)
				+ "/place/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Add place
			return placelistUC.addPlace(placelistId, placeId, userId);
		});
	}

	/**
	 * Remove the place
	 *
	 * @param validator
	 * @param placelistId
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /placelist/{placelistId}/place/{placeId}
	@Override
	public PlaceDTO removePlaceFromPlacelist(RequestValidator validator, Integer placelistId, Integer placeId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Placelist Endpoint --> [DELETE] PlaceToPlacelist - /placelist/" + String.valueOf(placelistId)
				+ "/place/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check placelist ID
			if (!validator.checkID(placelistId)) {
				throw new InvalidRequestException("Invalid placelist ID");
			}
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Remove place
			return placelistUC.removePlace(placelistId, placeId, userId);
		});
	}

}
