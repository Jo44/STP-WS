package fr.stp_ws.domain.exception;

/**
 * Technical exception
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class TechnicalException extends Exception {

	private static final long serialVersionUID = 930448801449184469L;
	private String message;

	/**
	 * Constructor
	 *
	 * @param message
	 */
	public TechnicalException(String message) {
		this.message = message;
	}

	/* Getter / Setter */

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
