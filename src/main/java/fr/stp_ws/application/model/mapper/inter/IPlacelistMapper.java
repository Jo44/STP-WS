package fr.stp_ws.application.model.mapper.inter;

import java.util.List;

import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;

/**
 * Placelist mapper interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPlacelistMapper {

	/**
	 * Convert placelist entity to placelist DTO
	 *
	 * @param placelist
	 * @param placelistMode
	 * @param commentMode
	 * @return PlacelistDTO
	 */
	public PlacelistDTO toDTO(Placelist placelist, PlacelistMode placelistMode, CommentMode commentMode);

	/**
	 * Convert placelist entities to placelist DTOs
	 *
	 * @param placelists
	 * @param placelistMode
	 * @param commentMode
	 * @return List<PlacelistDTO>
	 */
	public List<PlacelistDTO> toDTOList(List<Placelist> placelists, PlacelistMode placelistMode,
			CommentMode commentMode);
}
