package fr.stp_ws.application.service.inter;

import java.util.List;

import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;

/**
 * Place enrichment service interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlaceEnrichmentService {

	/**
	 * Enrich place DTO with comments (according to the specified mode)
	 *
	 * @param place
	 * @param placeDTO
	 * @param commentMode
	 */
	public void enrichWithComments(Place place, PlaceDTO placeDTO, CommentMode commentMode);

	/**
	 * Enrich place DTO with photos (according to the specified mode)
	 *
	 * @param place
	 * @param placeDTO
	 * @param photoMode
	 */
	public void enrichWithPhotos(Place place, PlaceDTO placeDTO, PhotoMode photoMode);

	/**
	 * Get X most recent comments from a place
	 *
	 * @param comments
	 * @param limit
	 * @return List<CommentDTO>
	 */
	public List<CommentDTO> getTopComments(List<CommentPlace> comments, int limit);

	/**
	 * Get X most recent photos from a place
	 *
	 * @param photos
	 * @param limit
	 * @return List<PhotoDTO>
	 */
	public List<PhotoDTO> getTopPhotos(List<Photo> photos, int limit);
}
