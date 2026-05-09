package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.NotExistPhotoException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Not Exist Photo
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class NotExistPhotoExceptionMapper extends AbstractExceptionMapper<NotExistPhotoException> {

	/** Constructor */
	public NotExistPhotoExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "NOT_EXIST_PHOTO");
	}
}