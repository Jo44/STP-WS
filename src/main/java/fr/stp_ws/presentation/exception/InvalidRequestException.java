package fr.stp_ws.presentation.exception;

import fr.stp_ws.domain.exception.FunctionalException;

/**
 * Functional exception - Invalid request
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class InvalidRequestException extends FunctionalException {

	private static final long serialVersionUID = 930448801449184469L;

	/**
	 * Constructor
	 *
	 * @param message
	 */
	public InvalidRequestException(String message) {
		super(message);
	}
}
