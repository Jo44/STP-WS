package fr.stp_ws.application.model.mapper.inter;

import java.util.List;

import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;

/**
 * Basic placelist mapper interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IBasicPlacelistMapper {

	/**
	 * Convert placelist entity to placelist DTO (no comment / no place)
	 *
	 * @param placelist
	 * @return PlacelistDTO
	 */
	public PlacelistDTO toBasicDTO(Placelist placelist);

	/**
	 * Convert placelist entities to placelist DTOs (no comment / no place
	 *
	 * @param placelists
	 * @return List<PlacelistDTO>
	 */
	public List<PlacelistDTO> toBasicDTOList(List<Placelist> placelists);

	/**
	 * Convert placelist DTO to placelist entity
	 *
	 * @param placelistDTO
	 * @param user
	 * @return Placelist
	 */
	public Placelist toEntity(PlacelistDTO placelistDTO, User user);
}
