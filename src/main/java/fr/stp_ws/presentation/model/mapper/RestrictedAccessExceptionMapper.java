package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.RestrictedAccessException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Restricted Access
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class RestrictedAccessExceptionMapper extends AbstractExceptionMapper<RestrictedAccessException> {

	/** Constructor */
	public RestrictedAccessExceptionMapper() {
		super(ErrorCode.Unauthorized, "RESTRICTED_ACCESS");
	}
}