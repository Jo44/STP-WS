package fr.stp_ws.application.model.mapper.inter;

import java.util.List;

import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;

/**
 * Basic place mapper interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IBasicPlaceMapper {

	/**
	 * Convert place entity to place DTO (no comment / no photo)
	 *
	 * @param place
	 * @return PlaceDTO
	 */
	public PlaceDTO toBasicDTO(Place place);

	/**
	 * Convert place entities to place DTOs (no comment / no photo)
	 *
	 * @param places
	 * @return List<PlaceDTO>
	 */
	public List<PlaceDTO> toBasicDTOList(List<Place> places);

	/**
	 * Convert place DTO to place entity
	 *
	 * @param placeDTO
	 * @param user
	 * @return Place
	 */
	public Place toEntity(PlaceDTO placeDTO, User user);
}
