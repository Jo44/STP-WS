package fr.stp_ws.application.repository;

import java.util.List;

import fr.stp_ws.data.model.Place;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;

/**
 * Place repository interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlaceRepo {

	/**
	 * Get all places
	 *
	 * @param category
	 * @param mine
	 * @param owner
	 * @param type
	 * @param fromLat
	 * @param toLat
	 * @param fromLong
	 * @param toLong
	 * @return List<Place>
	 * @throws TechnicalException
	 */
	public List<Place> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type, Double fromLat,
			Double toLat, Double fromLong, Double toLong) throws TechnicalException;

	/**
	 * Get a place
	 *
	 * @param placeId
	 * @param commentMode
	 * @param photoMode
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Place get(Integer placeId, CommentMode commentMode, PhotoMode photoMode)
			throws FunctionalException, TechnicalException;

	/**
	 * Add a place
	 *
	 * @param place
	 * @return Place
	 * @throws TechnicalException
	 */
	public Place add(Place place) throws TechnicalException;

	/**
	 * Update a place
	 *
	 * @param place
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Place update(Place place) throws FunctionalException, TechnicalException;

	/**
	 * Delete a place
	 *
	 * @param placeId
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Place delete(Integer placeId) throws FunctionalException, TechnicalException;

	/**
	 * Count the number of places for a user
	 *
	 * @param userId
	 * @return Integer
	 * @throws TechnicalException
	 */
	public Integer count(Integer userId) throws TechnicalException;
}
