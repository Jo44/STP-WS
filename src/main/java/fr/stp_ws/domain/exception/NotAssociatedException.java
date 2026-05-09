package fr.stp_ws.domain.exception;

/**
 * Functional exception - Entity not associated
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class NotAssociatedException extends FunctionalException {

	private static final long serialVersionUID = 930448801449184469L;

	/**
	 * Constructor
	 *
	 * @param message
	 */
	public NotAssociatedException(String message) {
		super(message);
	}
}
