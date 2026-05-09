package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.AlreadyExistUserException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Already Exist User
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class AlreadyExistUserExceptionMapper extends AbstractExceptionMapper<AlreadyExistUserException> {

	/** Constructor */
	public AlreadyExistUserExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "ALREADY_EXIST_USER");
	}
}