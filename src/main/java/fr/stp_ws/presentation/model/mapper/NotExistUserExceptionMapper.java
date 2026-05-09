package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.NotExistUserException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Not Exist User
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class NotExistUserExceptionMapper extends AbstractExceptionMapper<NotExistUserException> {

	/** Constructor */
	public NotExistUserExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "NOT_EXIST_USER");
	}
}