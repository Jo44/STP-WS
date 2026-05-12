package fr.stp_ws.application.model.mapper.inter;

import fr.stp_ws.domain.model.dto.resource.CountDTO;

/**
 * Count mapper interface
 *
 * @author Jo44
 * @version 1.0 (12/05/2026)
 * @since 12/05/2026
 */
public interface ICountMapper {

	/**
	 * Convert count to DTO
	 *
	 * @param count
	 * @return CountDTO
	 */
	public CountDTO toDTO(Integer count);
}
