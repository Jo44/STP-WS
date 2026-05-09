package fr.stp_ws.data.repository;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import fr.stp_ws.application.repository.IBugReportRepo;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.data.model.BugReport;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.PersistenceException;

/**
 * Bug report repository implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class BugReportRepo implements IBugReportRepo {

	private static final Logger LOGGER = LogManager.getLogger(BugReportRepo.class);
	private final Hibernate hibernate;
	private final IUserRepo userRepo;

	/**
	 * Constructor
	 *
	 * @param hibernate
	 * @param userRepo
	 */
	@Inject
	public BugReportRepo(Hibernate hibernate, IUserRepo userRepo) {
		this.hibernate = hibernate;
		this.userRepo = userRepo;
	}

	/* Bug report - Add */

	/**
	 * Add a bug report
	 *
	 * @param description
	 * @param fileName
	 * @param userId
	 * @return BugReport
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public void add(String description, String fileName, Integer userId)
			throws FunctionalException, TechnicalException {
		Session session = hibernate.openSession();
		try {
			// Get user
			User user = userRepo.getById(userId);
			// Create bug report
			BugReport bugReport = new BugReport(null, description, fileName, new Timestamp(System.currentTimeMillis()),
					user);
			// Save bug report
			session.merge(bugReport);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Bug report added successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding bug report : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding bug report : {}", pex.getMessage());
			throw new TechnicalException("Unable to add bug report");
		}
	}
}
