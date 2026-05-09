package fr.stp_ws.application.repository;

import java.util.List;

import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;

/**
 * Placelist repository interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlacelistRepo {

	/**
	 * Get all placelists
	 *
	 * @param category
	 * @param mine
	 * @param owner
	 * @param type
	 * @return List<Placelist>
	 * @throws TechnicalException
	 */
	public List<Placelist> getAll(EntityCategory category, Boolean mine, Integer owner, EntityType type)
			throws TechnicalException;

	/**
	 * Get a placelist
	 *
	 * @param placelistId
	 * @param placelistMode
	 * @param commentMode
	 * @return Placelist
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Placelist get(Integer placelistId, PlacelistMode placelistMode, CommentMode commentMode)
			throws FunctionalException, TechnicalException;

	/**
	 * Add a placelist
	 *
	 * @param placelist
	 * @return Placelist
	 * @throws TechnicalException
	 */
	public Placelist add(Placelist placelist) throws TechnicalException;

	/**
	 * Update a placelist
	 *
	 * @param placelist
	 * @return Placelist
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Placelist update(Placelist placelist) throws FunctionalException, TechnicalException;

	/**
	 * Delete a placelist
	 *
	 * @param placelistId
	 * @return Placelist
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Placelist delete(Integer placelistId) throws FunctionalException, TechnicalException;

	/**
	 * Add a place to a placelist
	 *
	 * @param placelistId
	 * @param placeId
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Place addPlace(Integer placelistId, Integer placeId) throws FunctionalException, TechnicalException;

	/**
	 * Remove a place from a placelist
	 *
	 * @param placelistId
	 * @param placeId
	 * @return Place
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Place removePlace(Integer placelistId, Integer placeId) throws FunctionalException, TechnicalException;

	/**
	 * Count the number of placelists for a user
	 *
	 * @param userId
	 * @return Integer
	 * @throws TechnicalException
	 */
	public Integer count(Integer userId) throws TechnicalException;

	/**
	 * Count the number of places already associated with a placelist
	 *
	 * @param placelistId
	 * @return Integer
	 * @throws TechnicalException
	 */
	public Integer countPlacesIn(Integer placelistId) throws TechnicalException;
}
