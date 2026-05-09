package fr.stp_ws.presentation.model.miscellaneous;

/**
 * Error code enumeration
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public enum ErrorCode {
	Unauthorized(401), PreconditionFailed(412), ServiceUnavailable(503);

	private final int value;

	/**
	 * Constructor
	 *
	 * @param value
	 */
	ErrorCode(int value) {
		this.value = value;
	}

	/**
	 * To String
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		return String.valueOf(value);
	}

	/* Getter */

	public int get() {
		return value;
	}
}
