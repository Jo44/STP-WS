package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.AlreadyCommentedPlaceException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Already Commented Place
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class AlreadyCommentedPlaceExceptionMapper extends AbstractExceptionMapper<AlreadyCommentedPlaceException> {

	/** Constructor */
	public AlreadyCommentedPlaceExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "ALREADY_COMMENTED_PLACE");
	}
}