package fr.stp_ws.application.model.mapper.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.ICountMapper;
import fr.stp_ws.domain.model.dto.resource.CountDTO;

/**
 * Count mapper implementation
 *
 * @author Jo44
 * @version 1.0 (12/05/2026)
 * @since 12/05/2026
 */
public class CountMapper implements ICountMapper {

	private static final Logger LOGGER = LogManager.getLogger(CountMapper.class);

	/** Constructor */
	public CountMapper() {
		super();
	}

	/**
	 * Convert count to DTO
	 *
	 * @param count
	 * @return CountDTO
	 */
	@Override
	public CountDTO toDTO(Integer count) {
		if (count == null) {
			return null;
		}
		LOGGER.debug("Converting count to DTO");
		CountDTO countDTO = new CountDTO();
		countDTO.setCount(count);
		return countDTO;
	}
}
