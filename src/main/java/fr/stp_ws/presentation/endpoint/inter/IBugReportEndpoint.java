package fr.stp_ws.presentation.endpoint.inter;

import java.io.InputStream;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import fr.stp_ws.domain.model.miscellaneous.BugReportResponse;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Bug report endpoint interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.MULTIPART_FORM_DATA)
public interface IBugReportEndpoint {

	/**
	 * Collect bug report
	 *
	 * @param description
	 * @param bugFile
	 * @param fileDetail
	 * @return BugReportResponse
	 */
	@POST
	public BugReportResponse collectBugReport(@FormDataParam("description") String description,
			@FormDataParam("bug_file") InputStream bugFile,
			@FormDataParam("bug_file") FormDataContentDisposition fileDetail);
}
