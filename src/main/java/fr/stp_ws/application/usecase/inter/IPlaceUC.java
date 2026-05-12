package fr.stp_ws.application.usecase.inter;

import java.util.List;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;

/**
 * Place use-cases interface
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
public interface IPlaceUC {

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
	public List<PlaceDTO> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type, Double fromLat,
			Double toLat, Double fromLong, Double toLong) throws TechnicalException;

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
	public PlaceDTO get(Integer placeId, Integer owner, CommentMode commentMode, PhotoMode photoMode)
			throws FunctionalException, TechnicalException;

	/**
	 * Count owner places
	 *
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countOwnerPlaces(Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Add the place
	 *
	 * @param placeDTO
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public PlaceDTO add(PlaceDTO placeDTO, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Update the place
	 *
	 * @param placeDTO
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public PlaceDTO update(PlaceDTO placeDTO, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Delete the place
	 *
	 * @param placeId
	 * @param owner
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public PlaceDTO delete(Integer placeId, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Get all comments
	 *
	 * @param placeId
	 * @param owner
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public List<CommentDTO> getComments(Integer placeId, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Count owner comment
	 *
	 * @param placeId
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countOwnerComment(Integer placeId, Integer owner) throws FunctionalException, TechnicalException;

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
	public CommentDTO addComment(Integer placeId, CommentDTO commentDTO, Integer owner)
			throws FunctionalException, TechnicalException;

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
	public CommentDTO deleteComment(Integer placeId, Integer commentId, Integer owner)
			throws FunctionalException, TechnicalException;

	/**
	 * Get all photos
	 *
	 * @param placeId
	 * @param owner
	 * @return List<PhotoDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public List<PhotoDTO> getPhotos(Integer placeId, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Count all photos
	 *
	 * @param placeId
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countPhotos(Integer placeId, Integer owner) throws FunctionalException, TechnicalException;

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
	public PhotoDTO addPhoto(Integer placeId, PhotoDTO photoDTO, Integer owner)
			throws FunctionalException, TechnicalException;

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
	public PhotoDTO deletePhoto(Integer placeId, Integer photoId, Integer owner)
			throws FunctionalException, TechnicalException;
}
