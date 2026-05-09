package fr.stp_ws.application.usecase.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.repository.IBugReportRepo;
import fr.stp_ws.application.usecase.inter.IBugReportUC;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.miscellaneous.BugReportResponse;
import jakarta.inject.Inject;

/**
 * Bug report use-cases implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class BugReportUC implements IBugReportUC {

	private static final Logger LOGGER = LogManager.getLogger(BugReportUC.class);
	private static final String BASE_REPORTS_PATH = System.getenv("SAVETHEPLACE") + File.separator + "reports";
	private final IBugReportRepo bugReportRepo;

	/** Constructor */
	@Inject
	public BugReportUC(IBugReportRepo bugReportRepo) {
		this.bugReportRepo = bugReportRepo;
	}

	/**
	 * Collect bug report
	 *
	 * @param userId
	 * @param description
	 * @param logFile
	 * @param fileDetail
	 * @return BugReportResponse
	 * @throws FunctionalException
	 * @throws TechnicalExceptionq
	 */
	@Override
	public BugReportResponse collectBugReport(Integer userId, String description, InputStream logFile, String fileName)
			throws FunctionalException, TechnicalException {
		BugReportResponse bugReportResponse = null;
		try {
			// Retrieve user report path
			String userReportPath = BASE_REPORTS_PATH + File.separator + userId;
			Path reportDirPath = Paths.get(userReportPath);
			if (!Files.exists(reportDirPath)) {
				Files.createDirectories(reportDirPath);
			}
			// Generate destination path
			String filePath = userReportPath + File.separator + fileName;
			// Save file
			try (FileOutputStream outputStream = new FileOutputStream(new File(filePath))) {
				byte[] buffer = new byte[4096];
				int bytesRead;
				while ((bytesRead = logFile.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}
			}
			LOGGER.info("New bug report file saved at : {}", filePath);
			// Save bug report
			bugReportRepo.add(description, fileName, userId);
			LOGGER.info("New bug report added to database for user ID : {}", userId);
			LOGGER.info(fileName);
			// Initialize response
			bugReportResponse = new BugReportResponse(true);
		} catch (IOException ioex) {
			String message = "Unable to save bug report file";
			LOGGER.error(message, ioex);
			throw new TechnicalException(message);
		}
		return bugReportResponse;
	}
}
