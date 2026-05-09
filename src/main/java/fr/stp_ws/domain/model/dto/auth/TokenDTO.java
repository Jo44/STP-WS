package fr.stp_ws.domain.model.dto.auth;

/**
 * Token DTO (Refresh Token / STPT / JWT)
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class TokenDTO {

	private String token;

	/** Constructor */
	public TokenDTO() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param token
	 */
	public TokenDTO(String token) {
		this();
		this.token = token;
	}

	/* Getter / Setter */

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
