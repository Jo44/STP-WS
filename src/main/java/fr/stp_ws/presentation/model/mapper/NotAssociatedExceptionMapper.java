package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.NotAssociatedException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Not Associated
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class NotAssociatedExceptionMapper extends AbstractExceptionMapper<NotAssociatedException> {

	/** Constructor */
	public NotAssociatedExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "NOT_ASSOCIATED");
	}
}