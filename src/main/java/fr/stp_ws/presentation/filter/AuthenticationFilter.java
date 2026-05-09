package fr.stp_ws.presentation.filter;

import java.io.IOException;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.security.ITokenService;
import fr.stp_ws.presentation.exception.InvalidRequestException;
import fr.stp_ws.presentation.exception.NotAuthorizedException;
import fr.stp_ws.presentation.model.miscellaneous.ErrorCode;
import fr.stp_ws.presentation.model.miscellaneous.ErrorResponse;
import fr.stp_ws.presentation.security.TokenSecurityContext;
import fr.stp_ws.presentation.security.UserPrincipal;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.ext.Provider;

/**
 * Authentication filter
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

	private static final Logger LOGGER = LogManager.getLogger(AuthenticationFilter.class);
	private final ITokenService tokenService;
	private static final Set<String> AUTH_PATHS = Set.of("api/auth/state", "api/auth/signup", "api/auth/login",
			"api/auth/refresh-stpt", "api/auth/refresh-jwt", "api/auth/logout", "auth/state", "auth/signup",
			"auth/login", "auth/refresh-stpt", "auth/refresh-jwt", "auth/logout");

	/**
	 * Constructor
	 *
	 * @param tokenService
	 */
	@Inject
	public AuthenticationFilter(ITokenService tokenService) {
		this.tokenService = tokenService;
	}

	/**
	 * Filter
	 *
	 * @param requestContext
	 * @throws IOException
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		// Ignore requests related to authentication
		String path = requestContext.getUriInfo().getPath();
		if (isAuthenticationPath(path)) {
			LOGGER.debug("-> Authentication request => Access granted");
			return;
		}
		try {
			// Check request authorization
			String authHeader = requestContext.getHeaderString("Authorization");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				throw new InvalidRequestException("Invalid authorization");
			}
			// Retrieve JWT
			String token = authHeader.substring(7);
			if (token == null || token.trim().isEmpty()) {
				throw new InvalidRequestException("Invalid token");
			}
			// Retrieve user ID
			Integer userId = tokenService.getIDFromJWT(token);
			if (userId == null || userId <= 0) {
				throw new NotAuthorizedException("Invalid user");
			}
			// Build security principal
			UserPrincipal principal = new UserPrincipal(userId);
			SecurityContext baseContext = requestContext.getSecurityContext();
			boolean isSecure = baseContext != null ? baseContext.isSecure() : false;
			String scheme = baseContext != null ? baseContext.getAuthenticationScheme() : "Bearer";
			// Set security context
			TokenSecurityContext securityContext = new TokenSecurityContext(principal, isSecure, scheme);
			requestContext.setSecurityContext(securityContext);
			LOGGER.debug("-> Valid authentication => Access granted");
		} catch (Exception ex) {
			LOGGER.error("-> Error while authentication => {} => Access denied",
					ex.getMessage() != null ? ex.getMessage() : "Unknown error");
			requestContext.abortWith(buildErrorResponse(ex));
		}
	}

	/**
	 * Build JSON error response
	 *
	 * @param ex
	 * @return Response
	 */
	private Response buildErrorResponse(Exception ex) {
		Response response = null;
		final int statusUnauthorized = ErrorCode.Unauthorized.get();
		if (ex instanceof NotAuthorizedException naex) {
			response = Response.status(statusUnauthorized)
					.entity(new ErrorResponse("NOT_AUTHORIZED", naex.getMessage())).type(MediaType.APPLICATION_JSON)
					.build();
		} else if (ex instanceof InvalidRequestException irex) {
			response = Response.status(statusUnauthorized)
					.entity(new ErrorResponse("INVALID_REQUEST", irex.getMessage())).type(MediaType.APPLICATION_JSON)
					.build();
		} else {
			String message = ex.getMessage() != null ? ex.getMessage() : "Unauthorized";
			response = Response.status(statusUnauthorized).entity(new ErrorResponse("NOT_AUTHORIZED", message))
					.type(MediaType.APPLICATION_JSON).build();
		}
		return response;
	}

	/**
	 * Check if request path targets an auth endpoint
	 *
	 * @param path
	 * @return boolean
	 */
	private boolean isAuthenticationPath(String path) {
		String normalizedPath = path != null ? path.trim().toLowerCase() : "";
		if (normalizedPath.startsWith("/")) {
			normalizedPath = normalizedPath.substring(1);
		}
		if (normalizedPath.endsWith("/")) {
			normalizedPath = normalizedPath.substring(0, normalizedPath.length() - 1);
		}
		return AUTH_PATHS.contains(normalizedPath);
	}
}
