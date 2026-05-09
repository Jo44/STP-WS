package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Technical Exception
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class TechnicalExceptionMapper extends AbstractExceptionMapper<TechnicalException> {

	/** Constructor */
	public TechnicalExceptionMapper() {
		super(ErrorCode.ServiceUnavailable, "TECHNICAL_ERROR");
	}
}