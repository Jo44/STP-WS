package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.AlreadyAssociatedException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Already Associated
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class AlreadyAssociatedExceptionMapper extends AbstractExceptionMapper<AlreadyAssociatedException> {

	/** Constructor */
	public AlreadyAssociatedExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "ALREADY_ASSOCIATED");
	}
}