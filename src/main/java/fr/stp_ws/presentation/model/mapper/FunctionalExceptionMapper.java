package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Functional Exception
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class FunctionalExceptionMapper extends AbstractExceptionMapper<FunctionalException> {

	/** Constructor */
	public FunctionalExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "FUNCTIONAL_ERROR");
	}
}