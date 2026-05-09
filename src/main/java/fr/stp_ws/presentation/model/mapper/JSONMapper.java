package fr.stp_ws.presentation.model.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

/**
 * JSON mapper
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JSONMapper implements ContextResolver<ObjectMapper> {

	private final ObjectMapper objectMapper;

	/** Constructor */
	public JSONMapper() {
		this.objectMapper = new ObjectMapper();
		// Ensure expected timestamp serialization / deserialization
		objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, true);
		objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
		objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
		objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
	}

	/**
	 * Get context
	 *
	 * @param type
	 * @return ObjectMapper
	 */
	@Override
	public ObjectMapper getContext(Class<?> type) {
		return objectMapper;
	}
}
