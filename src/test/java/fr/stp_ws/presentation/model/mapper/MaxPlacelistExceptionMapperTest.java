package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.MaxPlacelistException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Max placelist exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Max placelist exception mapper tests")
class MaxPlacelistExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for max placelists")
	void shouldReturnPreconditionFailedForMaxPlacelists() {
		MaxPlacelistExceptionMapper mapper = new MaxPlacelistExceptionMapper();
		MaxPlacelistException exception = new MaxPlacelistException("Max placelists reached");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("MAX_PLACELISTS", errorResponse.getCode());
		assertEquals("Max placelists reached", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
