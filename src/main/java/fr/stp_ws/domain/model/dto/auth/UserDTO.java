package fr.stp_ws.domain.model.dto.auth;

/**
 * User DTO
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class UserDTO {

	private String name;
	private String email;
	private Boolean tourist;
	private String refreshToken;
	private String stpt;
	private String jwt;

	/** Constructor */
	public UserDTO() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param email
	 * @param tourist
	 * @param refreshToken
	 * @param stpt
	 * @param jwt
	 */
	public UserDTO(String name, String email, Boolean tourist, String refreshToken, String stpt, String jwt) {
		this();
		this.name = name;
		this.email = email;
		this.tourist = tourist;
		this.refreshToken = refreshToken;
		this.stpt = stpt;
		this.jwt = jwt;
	}

	/**
	 * To String
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ Name : ");
		if (name != null) {
			sb.append(name);
		} else {
			sb.append("null");
		}
		sb.append(" - Email : ");
		if (email != null) {
			sb.append(email);
		} else {
			sb.append("null");
		}
		sb.append(" - Tourist : ");
		sb.append(String.valueOf(tourist));
		sb.append(" - Refresh Token : ");
		if (refreshToken != null) {
			sb.append(refreshToken);
		} else {
			sb.append("null");
		}
		sb.append(" - STPT : ");
		if (stpt != null) {
			sb.append(stpt);
		} else {
			sb.append("null");
		}
		sb.append(" - JWT : ");
		if (jwt != null) {
			sb.append(jwt);
		} else {
			sb.append("null");
		}
		sb.append(" ]");
		return sb.toString();
	}

	/* Getters / Setters */

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getTourist() {
		return tourist;
	}

	public void setTourist(Boolean tourist) {
		this.tourist = tourist;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getStpt() {
		return stpt;
	}

	public void setStpt(String stpt) {
		this.stpt = stpt;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}
