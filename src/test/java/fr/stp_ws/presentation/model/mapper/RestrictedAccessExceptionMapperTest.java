package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.RestrictedAccessException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Restricted access exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Restricted access exception mapper tests")
class RestrictedAccessExceptionMapperTest {

	@Test
	@DisplayName("Should return unauthorized for restricted access")
	void shouldReturnUnauthorizedForRestrictedAccess() {
		// Given
		RestrictedAccessExceptionMapper mapper = new RestrictedAccessExceptionMapper();
		RestrictedAccessException exception = new RestrictedAccessException("Access denied");
		// When
		Response response = mapper.toResponse(exception);
		// Then
		assertNotNull(response);
		assertEquals(ErrorCode.Unauthorized.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertNotNull(errorResponse);
		assertEquals("RESTRICTED_ACCESS", errorResponse.getCode());
		assertEquals("Access denied", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
