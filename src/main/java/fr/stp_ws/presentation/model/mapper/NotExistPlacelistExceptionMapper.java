package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.domain.exception.NotExistPlacelistException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import jakarta.ws.rs.ext.Provider;

/**
 * Exception mapper - Not Exist Placelist
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
public class NotExistPlacelistExceptionMapper extends AbstractExceptionMapper<NotExistPlacelistException> {

	/** Constructor */
	public NotExistPlacelistExceptionMapper() {
		super(ErrorCode.PreconditionFailed, "NOT_EXIST_PLACELIST");
	}
}