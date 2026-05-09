package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.MaxPlaceException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Max place exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Max place exception mapper tests")
class MaxPlaceExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for max places")
	void shouldReturnPreconditionFailedForMaxPlaces() {
		MaxPlaceExceptionMapper mapper = new MaxPlaceExceptionMapper();
		MaxPlaceException exception = new MaxPlaceException("Max places reached");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("MAX_PLACES", errorResponse.getCode());
		assertEquals("Max places reached", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
