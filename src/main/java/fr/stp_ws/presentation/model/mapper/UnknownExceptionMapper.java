package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.presentation.exception.UnknownException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Unknown Exception
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class UnknownExceptionMapper extends AbstractExceptionMapper<UnknownException> {

	/** Constructor */
	public UnknownExceptionMapper() {
		super(ErrorCode.ServiceUnavailable, "UNKNOWN_ERROR");
	}
}