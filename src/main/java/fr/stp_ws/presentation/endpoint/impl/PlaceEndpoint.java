package fr.stp_ws.presentation.endpoint.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.usecase.inter.IPlaceUC;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.presentation.endpoint.inter.IPlaceEndpoint;
import fr.stp_ws.presentation.exception.InvalidRequestException;
import fr.stp_ws.presentation.security.SecurityContextUser;
import fr.stp_ws.presentation.validator.RequestValidator;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

/**
 * Place endpoints implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
@Path("/place")
public class PlaceEndpoint extends AbstractEndpoint implements IPlaceEndpoint {

	private static final Logger LOGGER = LogManager.getLogger(PlaceEndpoint.class);
	@Context
	private SecurityContext securityContext;
	private final IPlaceUC placeUC;

	/** Constructor */
	@Inject
	public PlaceEndpoint(IPlaceUC placeUC) {
		super();
		this.placeUC = placeUC;
	}

	/* Place - [GET] / [POST] / [PUT] / [DELETE] */

	/**
	 * Get all places (according to parameters)
	 *
	 * @param validator
	 * @param category
	 * @param mine
	 * @param type
	 * @param fromLat
	 * @param toLat
	 * @param fromLong
	 * @param toLong
	 * @return List<PlaceDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint:
	// /place/list/{category}/{mine}/{type}/{fromLat}/{toLat}/{fromLong}/{toLong}
	@Override
	public List<PlaceDTO> getPlaces(RequestValidator validator, String category, Boolean mine, String type,
			Double fromLat, Double toLat, Double fromLong, Double toLong)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [GET] Places - /place/list/" + category + "/" + String.valueOf(mine) + "/"
				+ type + "/" + String.valueOf(fromLat) + "/" + String.valueOf(toLat) + "/" + String.valueOf(fromLong)
				+ "/" + String.valueOf(toLong));
		return execute(LOGGER, () -> {
			List<PlaceDTO> places = new ArrayList<>();
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check area search parameters
			if (!validator.checkAreaSearch(fromLat, toLat, fromLong, toLong)) {
				throw new InvalidRequestException("Area search parameters are not valid");
			}
			// Retrieve places category and type
			EntityCategory foundCategory = EntityCategory.fromValue(category);
			EntityType foundType = EntityType.fromValue(type);
			// Based on the category
			switch (foundCategory) {
			// Retrieve all places (User and Tourist)
			case ALL -> {
				List<PlaceDTO> placesUser = placeUC.getAll(EntityCategory.USER, mine, userId, foundType, fromLat, toLat,
						fromLong, toLong);
				List<PlaceDTO> placesTourist = placeUC.getAll(EntityCategory.TOURIST, mine, userId, foundType, fromLat,
						toLat, fromLong, toLong);
				places.addAll(placesUser);
				places.addAll(placesTourist);
			}
			// Retrieve all places (User or Tourist)
			case USER, TOURIST ->
				places = placeUC.getAll(foundCategory, mine, userId, foundType, fromLat, toLat, fromLong, toLong);
			}
			// Return DTOs
			return places;
		});
	}

	/**
	 * Get the place
	 *
	 * @param validator
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/{placeId}
	@Override
	public PlaceDTO getPlace(RequestValidator validator, Integer placeId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [GET] Place - /place/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Retrieve place
			return placeUC.get(placeId, userId, CommentMode.MAX_3, PhotoMode.MAX_3);
		});
	}

	/**
	 * Add the place
	 *
	 * @param validator
	 * @param placeDTO
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place
	@Override
	public PlaceDTO addPlace(RequestValidator validator, PlaceDTO placeDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [POST] Place - /place");
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place
			if (!validator.checkPlace(placeDTO)) {
				throw new InvalidRequestException("Invalid place");
			}
			// Add place
			return placeUC.add(placeDTO, userId);
		});
	}

	/**
	 * Update the place
	 *
	 * @param validator
	 * @param placeDTO
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place
	@Override
	public PlaceDTO updatePlace(RequestValidator validator, PlaceDTO placeDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [PUT] Place - /place");
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place
			if (!validator.checkPlace(placeDTO)) {
				throw new InvalidRequestException("Invalid place");
			}
			// Update place
			return placeUC.update(placeDTO, userId);
		});
	}

	/**
	 * Delete the place
	 *
	 * @param validator
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/{placeId}
	@Override
	public PlaceDTO deletePlace(RequestValidator validator, Integer placeId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [DELETE] Place - /place/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Delete place
			return placeUC.delete(placeId, userId);
		});
	}

	/* Comment - [GET] / [POST] / [DELETE] */

	/**
	 * Get all comments
	 *
	 * @param validator
	 * @param placeId
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/comments/{placeId}
	@Override
	public List<CommentDTO> getComments(RequestValidator validator, Integer placeId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [GET] Comments - /place/comments/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Retrieve all comments
			// Return DTOs
			return placeUC.getComments(placeId, userId);
		});
	}

	/**
	 * Add the comment
	 *
	 * @param validator
	 * @param placeId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/comment/{placeId}
	@Override
	public CommentDTO addComment(RequestValidator validator, Integer placeId, CommentDTO commentDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [POST] Comment - /place/comment/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Check comment
			if (!validator.checkComment(commentDTO)) {
				throw new InvalidRequestException("Invalid comment");
			}
			// Add comment
			return placeUC.addComment(placeId, commentDTO, userId);
		});
	}

	/**
	 * Delete the comment
	 *
	 * @param validator
	 * @param placeId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/comment/{placeId}
	@Override
	public CommentDTO deleteComment(RequestValidator validator, Integer placeId, CommentDTO commentDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [DELETE] Comment - /place/comment/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Check comment
			if (!validator.checkComment(commentDTO)) {
				throw new InvalidRequestException("Invalid comment");
			}
			// Delete comment
			return placeUC.deleteComment(placeId, commentDTO.getId(), userId);
		});
	}

	/* Photo - [GET] / [POST] / [DELETE] */

	/**
	 * Get all photos
	 *
	 * @param validator
	 * @param placeId
	 * @return List<PhotoDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/photos/{placeId}
	@Override
	public List<PhotoDTO> getPhotos(RequestValidator validator, Integer placeId)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [GET] Photos - /place/photos/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Retrieve all photos
			// Return DTOs
			return placeUC.getPhotos(placeId, userId);
		});
	}

	/**
	 * Add the photo
	 *
	 * @param validator
	 * @param placeId
	 * @param photoDTO
	 * @return PhotoDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/photo/{placeId}
	@Override
	public PhotoDTO addPhoto(RequestValidator validator, Integer placeId, PhotoDTO photoDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [POST] Photo - /place/photo/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Check photo
			if (!validator.checkPhoto(photoDTO)) {
				throw new InvalidRequestException("Invalid photo");
			}
			// Add photo
			return placeUC.addPhoto(placeId, photoDTO, userId);
		});
	}

	/**
	 * Delete the photo
	 *
	 * @param validator
	 * @param placeId
	 * @param photoDTO
	 * @return PhotoDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	// Endpoint: /place/photo/{placeId}
	@Override
	public PhotoDTO deletePhoto(RequestValidator validator, Integer placeId, PhotoDTO photoDTO)
			throws FunctionalException, TechnicalException {
		LOGGER.info("Place Endpoint --> [DELETE] Photo - /place/photo/" + String.valueOf(placeId));
		return execute(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Check place ID
			if (!validator.checkID(placeId)) {
				throw new InvalidRequestException("Invalid place ID");
			}
			// Check photo
			if (!validator.checkPhoto(photoDTO)) {
				throw new InvalidRequestException("Invalid photo");
			}
			// Delete photo
			return placeUC.deletePhoto(placeId, photoDTO.getId(), userId);
		});
	}

}
