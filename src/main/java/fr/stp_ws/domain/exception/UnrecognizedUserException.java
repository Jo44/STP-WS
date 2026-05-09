package fr.stp_ws.domain.exception;

/**
 * Functional exception - Unrecognized user
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class UnrecognizedUserException extends FunctionalException {

	private static final long serialVersionUID = 930448801449184469L;

	/**
	 * Constructor
	 *
	 * @param message
	 */
	public UnrecognizedUserException(String message) {
		super(message);
	}
}
