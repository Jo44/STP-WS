package fr.stp_ws.presentation.filter;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;

/**
 * Character encoding filter
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
@Priority(Priorities.USER)
public class CharacterEncodingFilter implements ContainerResponseFilter {

	/**
	 * Filter response to ensure UTF-8 encoding with charset
	 *
	 * @param requestContext
	 * @param responseContext
	 */
	@Override
	public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
		// Get current media type
		MediaType mediaType = responseContext.getMediaType();
		if (mediaType != null) {
			// Add charset UTF-8 to all responses
			MediaType mediaTypeWithCharset = new MediaType(mediaType.getType(), mediaType.getSubtype(), "utf-8");
			responseContext.getHeaders().putSingle("Content-Type", mediaTypeWithCharset.toString());
		} else {
			// Default to JSON UTF-8 if no media type is set
			responseContext.getHeaders().putSingle("Content-Type", "application/json;charset=utf-8");
		}
	}
}
