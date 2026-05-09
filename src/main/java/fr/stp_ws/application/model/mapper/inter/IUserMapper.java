package fr.stp_ws.application.model.mapper.inter;

import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.auth.UserDTO;

/**
 * User mapper interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IUserMapper {

	/**
	 * Convert user entity to user DTO
	 *
	 * @param user
	 * @param refreshToken
	 * @param stpt
	 * @param jwt
	 * @return UserDTO
	 */
	public UserDTO toDTO(User user, String refreshToken, String stpt, String jwt);
}
