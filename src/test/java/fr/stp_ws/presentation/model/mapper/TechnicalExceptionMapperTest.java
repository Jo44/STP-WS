package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Technical exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Technical exception mapper tests")
class TechnicalExceptionMapperTest {

	@Test
	@DisplayName("Should return service unavailable for technical exception")
	void shouldReturnServiceUnavailableForTechnicalException() {
		// Given
		TechnicalExceptionMapper mapper = new TechnicalExceptionMapper();
		TechnicalException exception = new TechnicalException("Storage unavailable");
		// When
		Response response = mapper.toResponse(exception);
		// Then
		assertNotNull(response);
		assertEquals(ErrorCode.ServiceUnavailable.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertNotNull(errorResponse);
		assertEquals("TECHNICAL_ERROR", errorResponse.getCode());
		assertEquals("Storage unavailable", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
