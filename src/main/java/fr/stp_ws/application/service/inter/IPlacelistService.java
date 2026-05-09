package fr.stp_ws.application.service.inter;

import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.Entity;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyCommentedPlacelistException;
import fr.stp_ws.domain.exception.MaxPlaceByPlacelistException;
import fr.stp_ws.domain.exception.MaxPlacelistException;
import fr.stp_ws.domain.exception.RestrictedAccessException;

/**
 * Placelist service interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlacelistService {

	/**
	 * Check if the user is the owner of the entity
	 *
	 * @param user
	 * @param entity
	 * @return Boolean
	 */
	public Boolean isOwner(User user, Entity entity);

	/**
	 * Check if the entity is visible
	 *
	 * @param entity
	 * @return Boolean
	 */
	public Boolean isVisible(Entity entity);

	/**
	 * Check if the user can get the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	public void canGet(User user, Placelist placelist) throws RestrictedAccessException;

	/**
	 * Check if the user can add the placelist
	 *
	 * @param user
	 * @param placelist
	 * @param currentPlacelistCount
	 * @throws MaxPlacelistException
	 * @throws RestrictedAccessException
	 */
	public void canAdd(User user, Placelist placelist, Integer currentPlacelistCount)
			throws MaxPlacelistException, RestrictedAccessException;

	/**
	 * Check if the user can update the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	public void canUpdate(User user, Placelist placelist) throws RestrictedAccessException;

	/**
	 * Check if the user can delete the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	public void canDelete(User user, Placelist placelist) throws RestrictedAccessException;

	/**
	 * Check if the user can add the comment to the placelist
	 *
	 * @param user
	 * @param placelist
	 * @param hasCommentedPlacelist
	 * @throws AlreadyCommentedPlacelistException
	 * @throws RestrictedAccessException
	 */
	public void canAddComment(User user, Placelist placelist, Boolean hasCommentedPlacelist)
			throws AlreadyCommentedPlacelistException, RestrictedAccessException;

	/**
	 * Check if the user can delete the comment from the placelist
	 *
	 * @param user
	 * @param placelist
	 * @param comment
	 * @throws RestrictedAccessException
	 */
	public void canDeleteComment(User user, Placelist placelist, Comment comment) throws RestrictedAccessException;

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
	public void canAddPlace(User user, Placelist placelist, Place place, Integer currentPlaceCount)
			throws MaxPlaceByPlacelistException, RestrictedAccessException;

	/**
	 * Check if the user can remove the place from the placelist
	 *
	 * @param user
	 * @param placelist
	 * @throws RestrictedAccessException
	 */
	public void canRemovePlace(User user, Placelist placelist) throws RestrictedAccessException;
}
