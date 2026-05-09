package fr.stp_ws.application.service.inter;

import java.util.List;

import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;

/**
 * Placelist enrichment service interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlacelistEnrichmentService {

	/**
	 * Enrich placelist DTO with comments (according to the specified mode)
	 *
	 * @param placelist
	 * @param placelistDTO
	 * @param commentMode
	 */
	public void enrichWithComments(Placelist placelist, PlacelistDTO placelistDTO, CommentMode commentMode);

	/**
	 * Enrich placelist DTO with places (according to the specified mode)
	 *
	 * @param placelist
	 * @param placelistDTO
	 * @param placelistMode
	 */
	public void enrichWithPlaces(Placelist placelist, PlacelistDTO placelistDTO, PlacelistMode placelistMode);

	/**
	 * Get X most recent comments from a placelist
	 *
	 * @param comments
	 * @param limit
	 * @return List<CommentDTO>
	 */
	public List<CommentDTO> getTopComments(List<CommentPlacelist> comments, int limit);
}
