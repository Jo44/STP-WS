package fr.stp_ws.application.usecase.inter;

import java.util.List;

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

/**
 * Placelist use-cases interface
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
public interface IPlacelistUC {

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
	public List<PlacelistDTO> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type)
			throws TechnicalException;

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
	public PlacelistDTO get(Integer placelistId, Integer owner, PlacelistMode placelistMode, CommentMode commentMode)
			throws FunctionalException, TechnicalException;

	/**
	 * Count owner placelists
	 *
	 * @param owner
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public CountDTO countOwnerPlacelists(Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Add the placelist
	 *
	 * @param placelistDTO
	 * @param owner
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public PlacelistDTO add(PlacelistDTO placelistDTO, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Update the placelist
	 *
	 * @param placelistDTO
	 * @param owner
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public PlacelistDTO update(PlacelistDTO placelistDTO, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Delete the placelist
	 *
	 * @param placelistId
	 * @param owner
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public PlacelistDTO delete(Integer placelistId, Integer owner) throws FunctionalException, TechnicalException;

	/**
	 * Get all comments
	 *
	 * @param placelistId
	 * @param owner
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public List<CommentDTO> getComments(Integer placelistId, Integer owner)
			throws FunctionalException, TechnicalException;

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
			throws FunctionalException, TechnicalException;

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
	public CommentDTO addComment(Integer placelistId, CommentDTO commentDTO, Integer owner)
			throws FunctionalException, TechnicalException;

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
	public CommentDTO deleteComment(Integer placelistId, Integer commentId, Integer owner)
			throws FunctionalException, TechnicalException;

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
			throws FunctionalException, TechnicalException;

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
	public PlaceDTO addPlace(Integer placelistId, Integer placeId, Integer owner)
			throws FunctionalException, TechnicalException;

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
	public PlaceDTO removePlace(Integer placelistId, Integer placeId, Integer owner)
			throws FunctionalException, TechnicalException;
}
