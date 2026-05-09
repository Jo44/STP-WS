package fr.stp_ws.domain.model.miscellaneous;

import java.sql.Timestamp;

/**
 * Bug report response
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class BugReportResponse {

	private Boolean collected;
	private Timestamp timestamp;

	/** Constructor */
	public BugReportResponse() {
		this.timestamp = new Timestamp(System.currentTimeMillis());
	}

	/**
	 * Constructor
	 *
	 * @param collected
	 */
	public BugReportResponse(Boolean collected) {
		this();
		this.collected = collected;
	}

	/* Getters */

	public Boolean isCollected() {
		return collected;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}
}
