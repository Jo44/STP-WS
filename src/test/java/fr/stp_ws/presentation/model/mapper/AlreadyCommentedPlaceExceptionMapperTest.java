package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.AlreadyCommentedPlaceException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Already commented place exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Already commented place exception mapper tests")
class AlreadyCommentedPlaceExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for already commented place")
	void shouldReturnPreconditionFailedForAlreadyCommentedPlace() {
		AlreadyCommentedPlaceExceptionMapper mapper = new AlreadyCommentedPlaceExceptionMapper();
		AlreadyCommentedPlaceException exception = new AlreadyCommentedPlaceException("Already commented place");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("ALREADY_COMMENTED_PLACE", errorResponse.getCode());
		assertEquals("Already commented place", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
