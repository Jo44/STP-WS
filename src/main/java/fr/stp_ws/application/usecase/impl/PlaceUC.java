package fr.stp_ws.application.usecase.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.IBasicPlaceMapper;
import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.ICountMapper;
import fr.stp_ws.application.model.mapper.inter.IPhotoMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.repository.ICommentRepo;
import fr.stp_ws.application.repository.IPhotoRepo;
import fr.stp_ws.application.repository.IPlaceRepo;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.service.inter.IPlaceService;
import fr.stp_ws.application.usecase.inter.IPlaceUC;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.RestrictedAccessException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import jakarta.inject.Inject;

/**
 * Place use-cases implementation
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
public class PlaceUC implements IPlaceUC {

	private static final Logger LOGGER = LogManager.getLogger(PlaceUC.class);
	private final IUserRepo userRepo;
	private final IPlaceRepo placeRepo;
	private final ICommentRepo commentRepo;
	private final IPhotoRepo photoRepo;
	private final IPlaceService placeService;
	private final IPlaceMapper placeMapper;
	private final IBasicPlaceMapper basicPlaceMapper;
	private final ICommentMapper commentMapper;
	private final IPhotoMapper photoMapper;
	private final ICountMapper countMapper;

	/** Constructor */
	@Inject
	public PlaceUC(IUserRepo userRepo, IPlaceRepo placeRepo, ICommentRepo commentRepo, IPhotoRepo photoRepo,
			IPlaceService placeService, IPlaceMapper placeMapper, IBasicPlaceMapper basicPlaceMapper,
			ICommentMapper commentMapper, IPhotoMapper photoMapper, ICountMapper countMapper) {
		this.userRepo = userRepo;
		this.placeRepo = placeRepo;
		this.commentRepo = commentRepo;
		this.photoRepo = photoRepo;
		this.placeService = placeService;
		this.placeMapper = placeMapper;
		this.basicPlaceMapper = basicPlaceMapper;
		this.commentMapper = commentMapper;
		this.photoMapper = photoMapper;
		this.countMapper = countMapper;
	}

	/* Place - Get / Count */

	/**
	 * Get all places (according to the parameters)
	 *
	 * @param category
	 * @param mine
	 * @param owner
	 * @param type
	 * @param fromLat
	 * @param toLat
	 * @param fromLong
	 * @param toLong
	 * @return List<PlaceDTO>
	 * @throws TechnicalException
	 */
	@Override
	public List<PlaceDTO> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type, Double fromLat,
			Double toLat, Double fromLong, Double toLong) throws TechnicalException {
		LOGGER.debug("Retrieving all places");
		List<Place> places = null;
		// Based on the category
		switch (category) {
		// Retrieve all places (User and Tourist)
		case ALL -> {
			List<Place> placesUser = placeRepo.getAll(EntityCategory.USER, mine, owner, type, fromLat, toLat, fromLong,
					toLong);
			List<Place> placesTourist = placeRepo.getAll(EntityCategory.TOURIST, mine, owner, type, fromLat, toLat,
					fromLong, toLong);
			placesUser.addAll(placesTourist);
			places = placesUser;
		}
		// Retrieve all places (User)
		case USER -> {
			places = placeRepo.getAll(EntityCategory.USER, mine, owner, type, fromLat, toLat, fromLong, toLong);
		}
		// Retrieve all places (Tourist)
		case TOURIST -> {
			places = placeRepo.getAll(EntityCategory.TOURIST, mine, owner, type, fromLat, toLat, fromLong, toLong);
		}
		}
		// Convert places to DTOs
		return placeMapper.toDTOList(places, CommentMode.NONE, PhotoMode.NONE);
	}

	/**
	 * Get the place (according to the parameters)
	 *
	 * @param placeId
	 * @param owner
	 * @param commentMode
	 * @param photoMode
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlaceDTO get(Integer placeId, Integer owner, CommentMode commentMode, PhotoMode photoMode)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving the place");
		// Retrieve place
		Place place = placeRepo.get(placeId, commentMode, photoMode);
		// Retrieve user
		User user = userRepo.getById(owner);
		// Check permission
		placeService.canGet(user, place);
		// Convert place to DTO
		return placeMapper.toDTO(place, commentMode, photoMode);
	}

	/**
	 * Count owner places
	 *
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public CountDTO countOwnerPlaces(Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Counting owner places");
		// Retrieve current places count
		Integer currentPlacesCount = placeRepo.count(owner);
		// Convert count to DTO
		return countMapper.toDTO(currentPlacesCount);
	}

	/* Place - Add / Update / Delete */

	/**
	 * Add the place
	 *
	 * @param placeDTO
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlaceDTO add(PlaceDTO placeDTO, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Adding the place");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Convert DTO to place
		Place place = basicPlaceMapper.toEntity(placeDTO, user);
		// Retrieve current place count of the user
		Integer currentPlaceCount = placeRepo.count(owner);
		// Check permission
		placeService.canAdd(user, place, currentPlaceCount);
		// Add place
		Place addedPlace = placeRepo.add(place);
		// Convert place to DTO
		return placeMapper.toDTO(addedPlace, CommentMode.NONE, PhotoMode.NONE);
	}

	/**
	 * Update the place
	 *
	 * @param placeDTO
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlaceDTO update(PlaceDTO placeDTO, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Updating the place");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Convert DTO to place
		Place place = basicPlaceMapper.toEntity(placeDTO, user);
		// Retrieve existing place
		Place existingPlace = placeRepo.get(place.getId(), CommentMode.NONE, PhotoMode.NONE);
		// Check permission
		placeService.canUpdate(user, existingPlace);
		// Modify place
		existingPlace.setType(place.getType());
		existingPlace.setTimes(place.getTimes());
		existingPlace.setLatitude(place.getLatitude());
		existingPlace.setLongitude(place.getLongitude());
		existingPlace.setTitle(place.getTitle());
		existingPlace.setDescription(place.getDescription());
		existingPlace.setVisibility(place.getVisibility());
		existingPlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		// Update place
		Place updatedPlace = placeRepo.update(existingPlace);
		// Convert place to DTO
		return placeMapper.toDTO(updatedPlace, CommentMode.NONE, PhotoMode.NONE);
	}

	/**
	 * Delete the place
	 *
	 * @param placeId
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlaceDTO delete(Integer placeId, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Deleting the place");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Check permission
		placeService.canDelete(user, existingPlace);
		// Delete place
		Place deletedPlace = placeRepo.delete(existingPlace.getId());
		// Convert place to DTO
		return placeMapper.toDTO(deletedPlace, CommentMode.NONE, PhotoMode.NONE);
	}

	/* Comment - Get / Count / Add / Delete */

	/**
	 * Get all comments
	 *
	 * @param placeId
	 * @param owner
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public List<CommentDTO> getComments(Integer placeId, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving all comments");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Check permission
		placeService.canGet(user, existingPlace);
		// Retrieve all comments
		List<Comment> comments = commentRepo.getAll(placeId, owner, Place.class);
		// Convert comments to DTOs
		return commentMapper.toDTOList(comments);
	}

	/**
	 * Count owner comment
	 *
	 * @param placeId
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public CountDTO countOwnerComment(Integer placeId, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Counting owner comment");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Check permission
		placeService.canGet(user, existingPlace);
		// Retrieve all comments
		List<Comment> comments = commentRepo.getAll(placeId, owner, Place.class);
		// Retrieve current owner comment count
		Integer currentCommentCount = Math.toIntExact(comments.stream().filter(comment -> comment.getOwner() != null)
				.filter(comment -> owner.equals(comment.getOwner().getId())).count());
		// Convert count to DTO
		return countMapper.toDTO(currentCommentCount);
	}

	/**
	 * Add the comment to the place
	 *
	 * @param placeId
	 * @param commentDTO
	 * @param owner
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public CommentDTO addComment(Integer placeId, CommentDTO commentDTO, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Adding the comment to the place");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Check if the user has already commented the place
		Boolean hasCommentedPlace = commentRepo.hasCommented(placeId, owner, Place.class);
		// Check permission
		placeService.canAddComment(user, existingPlace, hasCommentedPlace);
		// Convert DTO to comment
		CommentPlace comment = (CommentPlace) commentMapper.toEntity(commentDTO, user, existingPlace, Place.class);
		// Add comment
		Comment addedComment = commentRepo.add(placeId, comment, Place.class);
		// Convert comment to DTO
		return commentMapper.toDTO(addedComment);
	}

	/**
	 * Delete the comment from the place
	 *
	 * @param placeId
	 * @param commentId
	 * @param owner
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public CommentDTO deleteComment(Integer placeId, Integer commentId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Deleting the comment from the place");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Retrieve comment
		Comment existingComment = commentRepo.get(commentId, Place.class);
		// Check permission
		placeService.canDeleteComment(user, existingPlace, existingComment);
		// Ensure comment belongs to the requested place (IDOR protection)
		if (existingComment != null && !((CommentPlace) existingComment).getPlace().getId().equals(placeId)) {
			throw new RestrictedAccessException("You cannot delete a comment not associated with the place");
		}
		// Delete comment
		CommentPlace deletedComment = (CommentPlace) commentRepo.delete(existingComment.getId(), Place.class);
		// Convert comment to DTO
		return commentMapper.toDTO(deletedComment);
	}

	/* Photo - Get / Count / Add / Delete */

	/**
	 * Get all photos
	 *
	 * @param placeId
	 * @param owner
	 * @return List<PhotoDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public List<PhotoDTO> getPhotos(Integer placeId, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving all photos");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Check permission
		placeService.canGet(user, existingPlace);
		// Retrieve all photos
		List<Photo> photos = photoRepo.getAll(placeId, owner);
		// Convert photos to DTOs
		return photoMapper.toDTOList(photos);
	}

	/**
	 * Count all photos
	 *
	 * @param placeId
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countPhotos(Integer placeId, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Counting photos");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Check permission
		placeService.canGet(user, existingPlace);
		// Retrieve current photos count
		Integer currentPhotosCount = photoRepo.count(existingPlace.getId());
		// Convert count to DTO
		return countMapper.toDTO(currentPhotosCount);
	}

	/**
	 * Add the photo to the place
	 *
	 * @param placeId
	 * @param photoDTO
	 * @param owner
	 * @return PhotoDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PhotoDTO addPhoto(Integer placeId, PhotoDTO photoDTO, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Adding the photo to the place");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Retrieve current photo count of the place
		Integer currentPhotoCount = photoRepo.count(placeId);
		// Check permission
		placeService.canAddPhoto(user, existingPlace, currentPhotoCount);
		// Convert DTO to photo
		Photo photo = photoMapper.toEntity(photoDTO, existingPlace);
		// Add photo
		Photo addedPhoto = photoRepo.add(photo);
		// Convert photo to DTO
		return photoMapper.toDTO(addedPhoto);
	}

	/**
	 * Delete the photo from the place
	 *
	 * @param placeId
	 * @param photoId
	 * @param owner
	 * @return PhotoDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PhotoDTO deletePhoto(Integer placeId, Integer photoId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Deleting the photo from the place");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Check permission
		placeService.canDeletePhoto(user, existingPlace);
		// Retrieve photo and ensure it belongs to the requested place (IDOR protection)
		Photo existingPhoto = photoRepo.get(photoId);
		if (!existingPhoto.getPlace().getId().equals(placeId)) {
			throw new RestrictedAccessException("You cannot delete a photo not associated with the place");
		}
		// Delete photo
		Photo deletedPhoto = photoRepo.delete(photoId);
		// Convert photo to DTO
		return photoMapper.toDTO(deletedPhoto);
	}
}
