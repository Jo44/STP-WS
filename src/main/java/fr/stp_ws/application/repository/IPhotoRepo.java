package fr.stp_ws.application.repository;

import java.util.List;

import fr.stp_ws.data.model.Photo;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;

/**
 * Photo repository interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPhotoRepo {

	/**
	 * Get all photos
	 *
	 * @param placeId
	 * @param owner
	 * @return List<Photo>
	 * @throws TechnicalException
	 */
	public List<Photo> getAll(Integer placeId, Integer owner) throws TechnicalException;

	/**
	 * Get a photo
	 *
	 * @param photoId
	 * @return Photo
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Photo get(Integer photoId) throws FunctionalException, TechnicalException;

	/**
	 * Add a photo
	 *
	 * @param photo
	 * @return Photo
	 * @throws TechnicalException
	 */
	public Photo add(Photo photo) throws TechnicalException;

	/**
	 * Delete a photo
	 *
	 * @param photoId
	 * @return Photo
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Photo delete(Integer photoId) throws FunctionalException, TechnicalException;

	/**
	 * Count the number of photos for a place
	 *
	 * @param placeId
	 * @return Integer
	 * @throws TechnicalException
	 */
	public Integer count(Integer placeId) throws TechnicalException;
}
