package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.AlreadyCommentedPlacelistException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Already commented placelist exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Already commented placelist exception mapper tests")
class AlreadyCommentedPlacelistExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for already commented placelist")
	void shouldReturnPreconditionFailedForAlreadyCommentedPlacelist() {
		AlreadyCommentedPlacelistExceptionMapper mapper = new AlreadyCommentedPlacelistExceptionMapper();
		AlreadyCommentedPlacelistException exception = new AlreadyCommentedPlacelistException(
				"Already commented placelist");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("ALREADY_COMMENTED_PLACELIST", errorResponse.getCode());
		assertEquals("Already commented placelist", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
