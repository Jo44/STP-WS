package fr.stp_ws.application.usecase.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.IBasicPlacelistMapper;
import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.ICountMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.model.mapper.inter.IPlacelistMapper;
import fr.stp_ws.application.repository.ICommentRepo;
import fr.stp_ws.application.repository.IPlaceRepo;
import fr.stp_ws.application.repository.IPlacelistRepo;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.service.inter.IPlacelistService;
import fr.stp_ws.application.usecase.inter.IPlacelistUC;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.RestrictedAccessException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import jakarta.inject.Inject;

/**
 * Placelist use-cases implementation
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
public class PlacelistUC implements IPlacelistUC {

	private static final Logger LOGGER = LogManager.getLogger(PlacelistUC.class);
	private final IUserRepo userRepo;
	private final IPlaceRepo placeRepo;
	private final IPlacelistRepo placelistRepo;
	private final ICommentRepo commentRepo;
	private final IPlacelistService placelistService;
	private final IPlaceMapper placeMapper;
	private final IPlacelistMapper placelistMapper;
	private final IBasicPlacelistMapper basicPlacelistMapper;
	private final ICommentMapper commentMapper;
	private final ICountMapper countMapper;

	/** Constructor */
	@Inject
	public PlacelistUC(IUserRepo userRepo, IPlaceRepo placeRepo, IPlacelistRepo placelistRepo, ICommentRepo commentRepo,
			IPlacelistService placelistService, IPlaceMapper placeMapper, IPlacelistMapper placelistMapper,
			IBasicPlacelistMapper basicPlacelistMapper, ICommentMapper commentMapper, ICountMapper countMapper) {
		this.userRepo = userRepo;
		this.placeRepo = placeRepo;
		this.placelistRepo = placelistRepo;
		this.commentRepo = commentRepo;
		this.placelistService = placelistService;
		this.placeMapper = placeMapper;
		this.placelistMapper = placelistMapper;
		this.basicPlacelistMapper = basicPlacelistMapper;
		this.commentMapper = commentMapper;
		this.countMapper = countMapper;
	}

	/* Placelist - Get / Count */

	/**
	 * Get all placelists (according to the parameters)
	 *
	 * @param category
	 * @param mine
	 * @param owner
	 * @param type
	 * @return List<PlacelistDTO>
	 * @throws TechnicalException
	 */
	@Override
	public List<PlacelistDTO> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type)
			throws TechnicalException {
		LOGGER.debug("Retrieving all placelists");
		List<Placelist> placelists = null;
		// Based on the category
		switch (category) {
		// Retrieve all placelists (User and Tourist)
		case ALL -> {
			List<Placelist> placelistsUser = placelistRepo.getAll(EntityCategory.USER, mine, owner, type);
			List<Placelist> placelistsTourist = placelistRepo.getAll(EntityCategory.TOURIST, mine, owner, type);
			placelistsUser.addAll(placelistsTourist);
			placelists = placelistsUser;
		}
		// Retrieve all placelists (User)
		case USER -> {
			placelists = placelistRepo.getAll(EntityCategory.USER, mine, owner, type);
		}
		// Retrieve all placelists (Tourist)
		case TOURIST -> {
			placelists = placelistRepo.getAll(EntityCategory.TOURIST, mine, owner, type);
		}
		}
		// Convert placelists to DTOs
		return placelistMapper.toDTOList(placelists, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
	}

	/**
	 * Get the placelist (according to the parameters)
	 *
	 * @param placelistId
	 * @param owner
	 * @param placelistMode
	 * @param commentMode
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlacelistDTO get(Integer placelistId, Integer owner, PlacelistMode placelistMode, CommentMode commentMode)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving the placelist");
		// Retrieve placelist
		Placelist placelist = placelistRepo.get(placelistId, placelistMode, commentMode);
		// Retrieve user
		User user = userRepo.getById(owner);
		// Check permission
		placelistService.canGet(user, placelist);
		// Convert placelist to DTO
		return placelistMapper.toDTO(placelist, placelistMode, commentMode);
	}

	/**
	 * Count owner placelists
	 *
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countOwnerPlacelists(Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Counting owner placelists");
		// Retrieve current placelists count
		Integer currentPlacelistsCount = placelistRepo.count(owner);
		// Convert count to DTO
		return countMapper.toDTO(currentPlacelistsCount);
	}

	/* Placelist - Add / Update / Delete */

	/**
	 * Add the placelist
	 *
	 * @param placelistDTO
	 * @param owner
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlacelistDTO add(PlacelistDTO placelistDTO, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Adding the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Convert DTO to placelist
		Placelist placelist = basicPlacelistMapper.toEntity(placelistDTO, user);
		// Retrieve current placelist count of the user
		Integer currentPlacelistCount = placelistRepo.count(owner);
		// Check permission
		placelistService.canAdd(user, placelist, currentPlacelistCount);
		// Add placelist
		Placelist addedPlacelist = placelistRepo.add(placelist);
		// Convert placelist to DTO
		return placelistMapper.toDTO(addedPlacelist, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
	}

	/**
	 * Update the placelist
	 *
	 * @param placelistDTO
	 * @param owner
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlacelistDTO update(PlacelistDTO placelistDTO, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Updating the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Convert DTO to placelist
		Placelist placelist = basicPlacelistMapper.toEntity(placelistDTO, user);
		// Retrieve existing placelist
		Placelist existingPlacelist = placelistRepo.get(placelist.getId(), PlacelistMode.WITHOUT_PLACES,
				CommentMode.NONE);
		// Check permission
		placelistService.canUpdate(user, existingPlacelist);
		// Modify placelist
		existingPlacelist.setType(placelist.getType());
		existingPlacelist.setTitle(placelist.getTitle());
		existingPlacelist.setDescription(placelist.getDescription());
		existingPlacelist.setVisibility(placelist.getVisibility());
		existingPlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
		// Update placelist
		Placelist updatedPlacelist = placelistRepo.update(existingPlacelist);
		// Convert placelist to DTO
		return placelistMapper.toDTO(updatedPlacelist, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
	}

	/**
	 * Delete the placelist
	 *
	 * @param placelistId
	 * @param owner
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlacelistDTO delete(Integer placelistId, Integer owner) throws FunctionalException, TechnicalException {
		LOGGER.debug("Deleting the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Check permission
		placelistService.canDelete(user, existingPlacelist);
		// Delete placelist
		Placelist deletedPlacelist = placelistRepo.delete(existingPlacelist.getId());
		// Convert placelist to DTO
		return placelistMapper.toDTO(deletedPlacelist, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
	}

	/* Comment - Get / Count / Add / Delete */

	/**
	 * Get all comments
	 *
	 * @param placelistId
	 * @param owner
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public List<CommentDTO> getComments(Integer placelistId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Retrieving all comments");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Check permission
		placelistService.canGet(user, existingPlacelist);
		// Retrieve all comments
		List<Comment> comments = commentRepo.getAll(placelistId, owner, Placelist.class);
		// Convert comments to DTOs
		return commentMapper.toDTOList(comments);
	}

	/**
	 * Count owner comment
	 *
	 * @param placelistId
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countOwnerComment(Integer placelistId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Counting owner comment");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Check permission
		placelistService.canGet(user, existingPlacelist);
		// Retrieve all comments
		List<Comment> comments = commentRepo.getAll(placelistId, owner, Placelist.class);
		// Retrieve current owner comment count
		Integer currentCommentCount = Math.toIntExact(comments.stream().filter(comment -> comment.getOwner() != null)
				.filter(comment -> owner.equals(comment.getOwner().getId())).count());
		// Convert count to DTO
		return countMapper.toDTO(currentCommentCount);
	}

	/**
	 * Add the comment to the placelist
	 *
	 * @param placelistId
	 * @param commentDTO
	 * @param owner
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public CommentDTO addComment(Integer placelistId, CommentDTO commentDTO, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Adding the comment to the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Check if the user has already commented the placelist
		Boolean hasCommentedPlacelist = commentRepo.hasCommented(placelistId, owner, Placelist.class);
		// Check permission
		placelistService.canAddComment(user, existingPlacelist, hasCommentedPlacelist);
		// Convert DTO to comment
		CommentPlacelist comment = (CommentPlacelist) commentMapper.toEntity(commentDTO, user, existingPlacelist,
				Placelist.class);
		// Add comment
		Comment addedComment = commentRepo.add(placelistId, comment, Placelist.class);
		// Convert comment to DTO
		return commentMapper.toDTO(addedComment);
	}

	/**
	 * Delete the comment from the placelist
	 *
	 * @param placelistId
	 * @param commentId
	 * @param owner
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public CommentDTO deleteComment(Integer placelistId, Integer commentId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Deleting the comment from the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Retrieve comment
		Comment existingComment = commentRepo.get(commentId, Placelist.class);
		// Check permission
		placelistService.canDeleteComment(user, existingPlacelist, existingComment);
		// Ensure comment belongs to the requested placelist (IDOR protection)
		if (existingComment != null
				&& !((CommentPlacelist) existingComment).getPlacelist().getId().equals(placelistId)) {
			throw new RestrictedAccessException("You cannot delete a comment not associated with the placelist");
		}
		// Delete comment
		CommentPlacelist deletedComment = (CommentPlacelist) commentRepo.delete(existingComment.getId(),
				Placelist.class);
		// Convert comment to DTO
		return commentMapper.toDTO(deletedComment);
	}

	/* Place - Count / Add / Remove */

	/**
	 * Count all places in the placelist
	 *
	 * @param placelistId
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countPlacesInPlacelist(Integer placelistId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Counting places in the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Check permission
		placelistService.canGet(user, existingPlacelist);
		// Retrieve current places in placelist count
		Integer currentPlacesInPlacelistCount = placelistRepo.countPlacesIn(placelistId);
		// Convert count to DTO
		return countMapper.toDTO(currentPlacesInPlacelistCount);
	}

	/**
	 * Add the place to the placelist
	 *
	 * @param placelistId
	 * @param placeId
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlaceDTO addPlace(Integer placelistId, Integer placeId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Adding the place to the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Retrieve place
		Place existingPlace = placeRepo.get(placeId, CommentMode.NONE, PhotoMode.NONE);
		// Retrieve current number of places already in the placelist
		Integer currentPlaceCount = placelistRepo.countPlacesIn(placelistId);
		// Check permission
		placelistService.canAddPlace(user, existingPlacelist, existingPlace, currentPlaceCount);
		// Add place
		Place addedPlace = placelistRepo.addPlace(placelistId, placeId);
		// Convert place to DTO
		return placeMapper.toDTO(addedPlace, CommentMode.NONE, PhotoMode.NONE);
	}

	/**
	 * Remove the place from the placelist
	 *
	 * @param placelistId
	 * @param placeId
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public PlaceDTO removePlace(Integer placelistId, Integer placeId, Integer owner)
			throws FunctionalException, TechnicalException {
		LOGGER.debug("Removing the place from the placelist");
		// Retrieve user
		User user = userRepo.getById(owner);
		// Retrieve placelist
		Placelist existingPlacelist = placelistRepo.get(placelistId, PlacelistMode.WITHOUT_PLACES, CommentMode.NONE);
		// Check permission
		placelistService.canRemovePlace(user, existingPlacelist);
		// Remove place
		Place removedPlace = placelistRepo.removePlace(placelistId, placeId);
		// Convert place to DTO
		return placeMapper.toDTO(removedPlace, CommentMode.NONE, PhotoMode.NONE);
	}
}
