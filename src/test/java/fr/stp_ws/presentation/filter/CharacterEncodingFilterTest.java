package fr.stp_ws.presentation.filter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

/**
 * Character encoding filter tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("Character encoding filter tests")
@ExtendWith(MockitoExtension.class)
class CharacterEncodingFilterTest {

	@Mock
	private ContainerRequestContext requestContext;
	@Mock
	private ContainerResponseContext responseContext;

	@Test
	@DisplayName("Should add UTF-8 charset to existing media type")
	void shouldAddUTF8CharsetToExistingMediaType() {
		// Given
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		when(responseContext.getMediaType()).thenReturn(MediaType.APPLICATION_JSON_TYPE);
		when(responseContext.getHeaders()).thenReturn(headers);
		// When
		filter.filter(requestContext, responseContext);
		// Then
		assertEquals("application/json;charset=utf-8", headers.getFirst("Content-Type"));
	}

	@Test
	@DisplayName("Should default to JSON UTF-8 when media type is missing")
	void shouldDefaultToJSONUTF8WhenMediaTypeIsMissing() {
		// Given
		CharacterEncodingFilter filter = new CharacterEncodingFilter();
		MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
		when(responseContext.getMediaType()).thenReturn(null);
		when(responseContext.getHeaders()).thenReturn(headers);
		// When
		filter.filter(requestContext, responseContext);
		// Then
		assertEquals("application/json;charset=utf-8", headers.getFirst("Content-Type"));
	}
}
