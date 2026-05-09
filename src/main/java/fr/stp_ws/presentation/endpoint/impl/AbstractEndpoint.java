package fr.stp_ws.presentation.endpoint.impl;

import org.apache.logging.log4j.Logger;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.presentation.exception.UnknownException;

/**
 * Abstract endpoint
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public abstract class AbstractEndpoint {

	/**
	 * Functional interface for endpoint actions
	 * 
	 * @param <T>
	 */
	@FunctionalInterface
	protected interface EndpointAction<T> {
		T execute() throws Exception;
	}

	/**
	 * Execute an endpoint action with error handling
	 * 
	 * @param <T>
	 * @param logger
	 * @param action
	 * @return <T>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	protected <T> T execute(Logger logger, EndpointAction<T> action) throws FunctionalException, TechnicalException {
		try {
			return action.execute();
		} catch (FunctionalException | TechnicalException cex) {
			logError(logger, cex);
			throw cex;
		} catch (Exception ex) {
			logError(logger, ex);
			throw new UnknownException();
		}
	}

	/**
	 * Execute an endpoint action with error handling and fallback
	 * 
	 * @param <T>
	 * @param logger
	 * @param action
	 * @param fallback
	 * @return <T>
	 */
	protected <T> T executeOrFallback(Logger logger, EndpointAction<T> action, T fallback) {
		try {
			return action.execute();
		} catch (Exception ex) {
			logError(logger, ex);
			return fallback;
		}
	}

	/**
	 * Log error message
	 * 
	 * @param logger
	 * @param ex
	 */
	private void logError(Logger logger, Exception ex) {
		logger.error(ex.getMessage() != null ? ex.getMessage() : "Unknown error");
	}
}
