package fr.stp_ws.application.model.mapper.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.IUserMapper;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.auth.UserDTO;

/**
 * User mapper implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class UserMapper implements IUserMapper {

	private static final Logger LOGGER = LogManager.getLogger(UserMapper.class);

	/** Constructor */
	public UserMapper() {
		super();
	}

	/**
	 * Convert user entity to user DTO
	 *
	 * @param user
	 * @param refreshToken
	 * @param stpt
	 * @param jwt
	 * @return UserDTO
	 */
	@Override
	public UserDTO toDTO(User user, String refreshToken, String stpt, String jwt) {
		if (user == null) {
			return null;
		}
		LOGGER.debug("Converting user to DTO");
		return new UserDTO(user.getName(), user.getEmail(), user.getTourist(), refreshToken, stpt, jwt);
	}
}
