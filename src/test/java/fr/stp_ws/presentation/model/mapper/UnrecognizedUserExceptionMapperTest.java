package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.UnrecognizedUserException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Unrecognized user exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Unrecognized user exception mapper tests")
class UnrecognizedUserExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for unrecognized user")
	void shouldReturnPreconditionFailedForUnrecognizedUser() {
		UnrecognizedUserExceptionMapper mapper = new UnrecognizedUserExceptionMapper();
		UnrecognizedUserException exception = new UnrecognizedUserException("Unrecognized user");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("UNRECOGNISED_USER", errorResponse.getCode());
		assertEquals("Unrecognized user", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
