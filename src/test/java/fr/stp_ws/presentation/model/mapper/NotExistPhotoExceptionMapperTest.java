package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.NotExistPhotoException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Not exist photo exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Not exist photo exception mapper tests")
class NotExistPhotoExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for missing photo")
	void shouldReturnPreconditionFailedForMissingPhoto() {
		NotExistPhotoExceptionMapper mapper = new NotExistPhotoExceptionMapper();
		NotExistPhotoException exception = new NotExistPhotoException("Photo does not exist");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("NOT_EXIST_PHOTO", errorResponse.getCode());
		assertEquals("Photo does not exist", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
