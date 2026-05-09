package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.NotExistCommentException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Not Exist Comment
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class NotExistCommentExceptionMapper extends AbstractExceptionMapper<NotExistCommentException> {

	/** Constructor */
	public NotExistCommentExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "NOT_EXIST_COMMENT");
	}
}