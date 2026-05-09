package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.MaxPlaceByPlacelistException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Max place by placelist exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Max place by placelist exception mapper tests")
class MaxPlaceByPlacelistExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for max places by placelist")
	void shouldReturnPreconditionFailedForMaxPlacesByPlacelist() {
		MaxPlaceByPlacelistExceptionMapper mapper = new MaxPlaceByPlacelistExceptionMapper();
		MaxPlaceByPlacelistException exception = new MaxPlaceByPlacelistException("Max places by placelist reached");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("MAX_PLACES_BY_PLACELIST", errorResponse.getCode());
		assertEquals("Max places by placelist reached", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
