package fr.stp_ws.domain.model.dto.auth;

/**
 * SignUp DTO
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class SignUpDTO {

	private String name;
	private String email;
	private String password;

	/** Constructor */
	public SignUpDTO() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param name
	 * @param email
	 * @param password
	 */
	public SignUpDTO(String name, String email, String password) {
		this();
		this.name = name;
		this.email = email;
		this.password = password;
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
