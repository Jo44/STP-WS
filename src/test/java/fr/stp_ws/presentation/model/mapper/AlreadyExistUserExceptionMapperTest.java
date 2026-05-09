package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.AlreadyExistUserException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Already exist user exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Already exist user exception mapper tests")
class AlreadyExistUserExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for already existing user")
	void shouldReturnPreconditionFailedForAlreadyExistingUser() {
		AlreadyExistUserExceptionMapper mapper = new AlreadyExistUserExceptionMapper();
		AlreadyExistUserException exception = new AlreadyExistUserException("User already exists");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("ALREADY_EXIST_USER", errorResponse.getCode());
		assertEquals("User already exists", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
