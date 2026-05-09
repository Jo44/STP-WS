package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.InvalidTokenException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Invalid Token
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class InvalidTokenExceptionMapper extends AbstractExceptionMapper<InvalidTokenException> {

	/** Constructor */
	public InvalidTokenExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "INVALID_TOKEN");
	}
}