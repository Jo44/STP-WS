package fr.stp_ws.domain.exception;

/**
 * Functional exception - Maximum photos reached
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class MaxPhotoException extends FunctionalException {

	private static final long serialVersionUID = 930448801449184469L;

	/**
	 * Constructor
	 *
	 * @param message
	 */
	public MaxPhotoException(String message) {
		super(message);
	}
}
