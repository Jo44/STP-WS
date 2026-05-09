package fr.stp_ws.application.service.inter;

import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.AlreadyCommentedPlaceException;
import fr.stp_ws.domain.exception.MaxPhotoException;
import fr.stp_ws.domain.exception.MaxPlaceException;
import fr.stp_ws.domain.exception.RestrictedAccessException;

/**
 * Place service interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlaceService {

	/**
	 * Check if the user is the owner of the place
	 *
	 * @param user
	 * @param place
	 * @return Boolean
	 */
	public Boolean isOwner(User user, Place place);

	/**
	 * Check if the place is visible
	 *
	 * @param place
	 * @return Boolean
	 */
	public Boolean isVisible(Place place);

	/**
	 * Check if the user can get the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	public void canGet(User user, Place place) throws RestrictedAccessException;

	/**
	 * Check if the user can add the place
	 *
	 * @param user
	 * @param place
	 * @param currentPlaceCount
	 * @throws MaxPlaceException
	 * @throws RestrictedAccessException
	 */
	public void canAdd(User user, Place place, Integer currentPlaceCount)
			throws MaxPlaceException, RestrictedAccessException;

	/**
	 * Check if the user can update the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	public void canUpdate(User user, Place place) throws RestrictedAccessException;

	/**
	 * Check if the user can delete the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	public void canDelete(User user, Place place) throws RestrictedAccessException;

	/**
	 * Check if the user can add the comment to the place
	 *
	 * @param user
	 * @param place
	 * @param hasCommentedPlace
	 * @throws AlreadyCommentedPlaceException
	 * @throws RestrictedAccessException
	 */
	public void canAddComment(User user, Place place, Boolean hasCommentedPlace)
			throws AlreadyCommentedPlaceException, RestrictedAccessException;

	/**
	 * Check if the user can delete the comment from the place
	 *
	 * @param user
	 * @param place
	 * @param comment
	 * @throws RestrictedAccessException
	 */
	public void canDeleteComment(User user, Place place, Comment comment) throws RestrictedAccessException;

	/**
	 * Check if the user can add the photo to the place
	 *
	 * @param user
	 * @param place
	 * @param currentPhotoCount
	 * @throws MaxPhotoException
	 * @throws RestrictedAccessException
	 */
	public void canAddPhoto(User user, Place place, Integer currentPhotoCount)
			throws MaxPhotoException, RestrictedAccessException;

	/**
	 * Check if the user can delete the photo from the place
	 *
	 * @param user
	 * @param place
	 * @throws RestrictedAccessException
	 */
	public void canDeletePhoto(User user, Place place) throws RestrictedAccessException;
}
