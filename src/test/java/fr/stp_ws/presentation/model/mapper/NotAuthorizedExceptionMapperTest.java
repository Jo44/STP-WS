package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.presentation.exception.NotAuthorizedException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Not authorized exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Not authorized exception mapper tests")
class NotAuthorizedExceptionMapperTest {

	@Test
	@DisplayName("Should return unauthorized error response")
	void shouldReturnUnauthorizedErrorResponse() {
		// Given
		NotAuthorizedExceptionMapper mapper = new NotAuthorizedExceptionMapper();
		NotAuthorizedException notAuthorizedException = new NotAuthorizedException("Missing authenticated principal");
		// When
		Response response = mapper.toResponse(notAuthorizedException);
		// Then
		assertNotNull(response);
		assertEquals(ErrorCode.Unauthorized.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		assertNotNull(response.getEntity());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("NOT_AUTHORIZED", errorResponse.getCode());
		assertEquals("Missing authenticated principal", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
