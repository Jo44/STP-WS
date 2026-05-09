package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.presentation.exception.UnknownException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Unknown exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Unknown exception mapper tests")
class UnknownExceptionMapperTest {

	@Test
	@DisplayName("Should return service unavailable for unknown exception")
	void shouldReturnServiceUnavailableForUnknownException() {
		UnknownExceptionMapper mapper = new UnknownExceptionMapper();
		UnknownException exception = new UnknownException();
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.ServiceUnavailable.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("UNKNOWN_ERROR", errorResponse.getCode());
		assertEquals("Unexpected internal error has occurred", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
