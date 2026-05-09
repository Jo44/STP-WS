package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import fr.stp_ws.domain.exception.AlreadyAssociatedException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Already associated exception mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Already associated exception mapper tests")
class AlreadyAssociatedExceptionMapperTest {

	@Test
	@DisplayName("Should return precondition failed for already associated entities")
	void shouldReturnPreconditionFailedForAlreadyAssociatedEntities() {
		AlreadyAssociatedExceptionMapper mapper = new AlreadyAssociatedExceptionMapper();
		AlreadyAssociatedException exception = new AlreadyAssociatedException("Already associated");
		Response response = mapper.toResponse(exception);
		assertNotNull(response);
		assertEquals(ErrorCode.PreconditionFailed.get(), response.getStatus());
		assertEquals(MediaType.APPLICATION_JSON, response.getMediaType().toString());
		ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
		assertEquals("ALREADY_ASSOCIATED", errorResponse.getCode());
		assertEquals("Already associated", errorResponse.getMessage());
		assertNotNull(errorResponse.getTimestamp());
	}
}
