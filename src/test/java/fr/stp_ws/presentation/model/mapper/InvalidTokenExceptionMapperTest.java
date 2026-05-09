package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.InvalidTokenException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Invalid token exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Invalid token exception mapper tests")
class InvalidTokenExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for invalid token")
	void shouldReturnPreconditionFailedForInvalidToken() {
		InvalidTokenExceptionMapper mapper = new InvalidTokenExceptionMapper();
		InvalidTokenException exception = new InvalidTokenException("Invalid token");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("INVALID_TOKEN", errorResponse.getCode());
		assertEquals("Invalid token", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
