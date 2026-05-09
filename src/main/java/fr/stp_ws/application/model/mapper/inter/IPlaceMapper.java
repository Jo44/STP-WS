package fr.stp_ws.application.model.mapper.inter;

import java.util.List;

import fr.stp_ws.data.model.Place;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;

/**
 * Place mapper interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlaceMapper {

	/**
	 * Convert place entity to place DTO
	 *
	 * @param place
	 * @param commentMode
	 * @param photoMode
	 * @return PlaceDTO
	 */
	public PlaceDTO toDTO(Place place, CommentMode commentMode, PhotoMode photoMode);

	/**
	 * Convert place entities to place DTOs
	 *
	 * @param places
	 * @param commentMode
	 * @param photoMode
	 * @return List<PlaceDTO>
	 */
	public List<PlaceDTO> toDTOList(List<Place> places, CommentMode commentMode, PhotoMode photoMode);
}
