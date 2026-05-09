package fr.stp_ws.domain.model.dto.auth;

/**
 * State DTO
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class StateDTO {

	private Boolean online;

	/** Constructor */
	public StateDTO() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param online
	 */
	public StateDTO(Boolean online) {
		this();
		this.online = online;
	}

	/* Getter / Setter */

	public Boolean getOnline() {
		return online;
	}

	public void setOnline(Boolean online) {
		this.online = online;
	}
}
