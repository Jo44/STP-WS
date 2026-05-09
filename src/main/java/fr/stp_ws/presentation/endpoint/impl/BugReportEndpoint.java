package fr.stp_ws.presentation.endpoint.impl;

import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import fr.stp_ws.application.usecase.inter.IBugReportUC;
import fr.stp_ws.domain.model.miscellaneous.BugReportResponse;
import fr.stp_ws.presentation.endpoint.inter.IBugReportEndpoint;
import fr.stp_ws.presentation.security.SecurityContextUser;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

/**
 * Bug report endpoint implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
@Path("/bug-report")
public class BugReportEndpoint extends AbstractEndpoint implements IBugReportEndpoint {

	private static final Logger LOGGER = LogManager.getLogger(BugReportEndpoint.class);
	@Context
	private SecurityContext securityContext;
	private final IBugReportUC bugReportUC;

	/** Constructor */
	@Inject
	public BugReportEndpoint(IBugReportUC bugReportUC) {
		super();
		this.bugReportUC = bugReportUC;
	}

	/**
	 * Collect bug report
	 *
	 * @param description
	 * @param bugFile
	 * @param fileDetail
	 * @return BugReportResponse
	 */
	// Endpoint : /bug-report
	@Override
	public BugReportResponse collectBugReport(String description, InputStream bugFile,
			FormDataContentDisposition fileDetail) {
		LOGGER.info("Bug Report Endpoint --> [POST] - /bug-report");
		BugReportResponse bugReported = executeOrFallback(LOGGER, () -> {
			// Retrieve user ID
			Integer userId = SecurityContextUser.getUserID(securityContext);
			// Collect bug report
			return bugReportUC.collectBugReport(userId, description, bugFile, fileDetail.getFileName());
		}, new BugReportResponse(false));
		// Check response to avoid null values
		if (bugReported == null) {
			bugReported = new BugReportResponse(false);
		}
		// Return response
		return bugReported;
	}

}
