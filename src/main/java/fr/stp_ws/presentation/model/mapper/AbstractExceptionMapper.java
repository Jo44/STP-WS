package fr.stp_ws.presentation.model.mapper;

import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

/**
 * Abstract exception mapper
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public abstract class AbstractExceptionMapper<T extends Exception> implements ExceptionMapper<T> {

	private final ErrorCode status;
	private final String code;

	/**
	 * Constructor
	 *
	 * @param status
	 * @param code
	 */
	protected AbstractExceptionMapper(ErrorCode status, String code) {
		super();
		this.status = status;
		this.code = code;
	}

	/**
	 * Convert exception to HTTP response
	 *
	 * @param exception
	 * @return Response
	 */
	@Override
	public Response toResponse(T exception) {
		return Response.status(status.get()).entity(new ErrorResponse(code, exception.getMessage()))
				.type(MediaType.APPLICATION_JSON).build();
	}
}
