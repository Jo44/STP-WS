package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.MaxPlaceException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Max Place
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class MaxPlaceExceptionMapper extends AbstractExceptionMapper<MaxPlaceException> {

	/** Constructor */
	public MaxPlaceExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "MAX_PLACES");
	}
}