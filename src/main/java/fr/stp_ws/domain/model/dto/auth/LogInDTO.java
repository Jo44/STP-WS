package fr.stp_ws.domain.model.dto.auth;

/**
 * LogIn DTO
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class LogInDTO {

	private String email;
	private String password;
	private String googleToken;

	/** Constructor */
	public LogInDTO() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param email
	 * @param password
	 * @param googleToken
	 */
	public LogInDTO(String email, String password, String googleToken) {
		this();
		this.email = email;
		this.password = password;
		this.googleToken = googleToken;
	}

	/* Getters / Setters */

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getGoogleToken() {
		return googleToken;
	}

	public void setGoogleToken(String googleToken) {
		this.googleToken = googleToken;
	}
}
