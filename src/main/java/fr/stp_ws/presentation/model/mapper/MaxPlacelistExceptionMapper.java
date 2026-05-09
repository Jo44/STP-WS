package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.MaxPlacelistException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Max Placelist
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class MaxPlacelistExceptionMapper extends AbstractExceptionMapper<MaxPlacelistException> {

	/** Constructor */
	public MaxPlacelistExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "MAX_PLACELISTS");
	}
}