package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.UnrecognizedUserException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Unrecognized User
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class UnrecognizedUserExceptionMapper extends AbstractExceptionMapper<UnrecognizedUserException> {

	/** Constructor */
	public UnrecognizedUserExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "UNRECOGNISED_USER");
	}
}