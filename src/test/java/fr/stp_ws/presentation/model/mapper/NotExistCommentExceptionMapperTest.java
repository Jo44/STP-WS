package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.NotExistCommentException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Not exist comment exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Not exist comment exception mapper tests")
class NotExistCommentExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for missing comment")
	void shouldReturnPreconditionFailedForMissingComment() {
		NotExistCommentExceptionMapper mapper = new NotExistCommentExceptionMapper();
		NotExistCommentException exception = new NotExistCommentException("Comment does not exist");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("NOT_EXIST_COMMENT", errorResponse.getCode());
		assertEquals("Comment does not exist", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
