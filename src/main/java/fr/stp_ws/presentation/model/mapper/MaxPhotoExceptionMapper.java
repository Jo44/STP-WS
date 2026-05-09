package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.MaxPhotoException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Max Photo
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class MaxPhotoExceptionMapper extends AbstractExceptionMapper<MaxPhotoException> {

	/** Constructor */
	public MaxPhotoExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "MAX_PHOTOS");
	}
}