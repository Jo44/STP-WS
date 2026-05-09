package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.presentation.exception.NotAuthorizedException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Not Authorized
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class NotAuthorizedExceptionMapper extends AbstractExceptionMapper<NotAuthorizedException> {

	/** Constructor */
	public NotAuthorizedExceptionMapper() {
		super(ErrorCode.Unauthorized, "NOT_AUTHORIZED");
	}
}