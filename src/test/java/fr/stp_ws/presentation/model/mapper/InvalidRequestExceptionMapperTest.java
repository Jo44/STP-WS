package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.presentation.exception.InvalidRequestException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Invalid request exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Invalid request exception mapper tests")
class InvalidRequestExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for invalid request")
	void shouldReturnPreconditionFailedForInvalidRequest() {
		// Given
		InvalidRequestExceptionMapper mapper = new InvalidRequestExceptionMapper();
		InvalidRequestException exception = new InvalidRequestException("Invalid payload");
		// When
		Response response = mapper.toResponse(exception);
		// Then
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertNotNull(errorResponse);
		assertEquals("INVALID_REQUEST", errorResponse.getCode());
		assertEquals("Invalid payload", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
