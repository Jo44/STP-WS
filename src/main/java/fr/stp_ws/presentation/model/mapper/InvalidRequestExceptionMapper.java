package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.presentation.exception.InvalidRequestException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Invalid Request
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class InvalidRequestExceptionMapper extends AbstractExceptionMapper<InvalidRequestException> {

	/** Constructor */
	public InvalidRequestExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "INVALID_REQUEST");
	}
}