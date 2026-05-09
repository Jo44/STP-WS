package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.MaxPlaceByPlacelistException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Max Place By Placelist
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class MaxPlaceByPlacelistExceptionMapper extends AbstractExceptionMapper<MaxPlaceByPlacelistException> {

	/** Constructor */
	public MaxPlaceByPlacelistExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "MAX_PLACES_BY_PLACELIST");
	}
}