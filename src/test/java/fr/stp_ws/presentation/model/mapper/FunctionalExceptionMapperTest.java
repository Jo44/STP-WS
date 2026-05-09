package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Functional exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Functional exception mapper tests")
class FunctionalExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for functional exception")
	void shouldReturnPreconditionFailedForFunctionalException() {
		FunctionalExceptionMapper mapper = new FunctionalExceptionMapper();
		FunctionalException exception = new FunctionalException("Functional error");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("FUNCTIONAL_ERROR", errorResponse.getCode());
		assertEquals("Functional error", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
