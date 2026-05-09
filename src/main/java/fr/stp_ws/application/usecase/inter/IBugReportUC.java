package fr.stp_ws.application.usecase.inter;

import java.io.InputStream;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.BugReportResponse;

/**
 * Bug report use-cases interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IBugReportUC {

	/**
	 * Collect bug report
	 *
	 * @param userId
	 * @param description
	 * @param logFile
	 * @param fileDetail
	 * @return BugReportResponse
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public BugReportResponse collectBugReport(Integer userId, String description, InputStream logFile, String fileName)
			throws FunctionalException, TechnicalException;
}
