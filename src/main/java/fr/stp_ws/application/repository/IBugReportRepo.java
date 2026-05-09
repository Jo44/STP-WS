package fr.stp_ws.application.repository;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;

/**
 * Bug report repository interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IBugReportRepo {

	/**
	 * Add a bug report
	 *
	 * @param description
	 * @param fileName
	 * @param userId
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public void add(String description, String fileName, Integer userId) throws FunctionalException, TechnicalException;
}
