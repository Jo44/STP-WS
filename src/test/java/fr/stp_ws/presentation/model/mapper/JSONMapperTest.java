package fr.stp_ws.presentation.model.mapper;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * JSON mapper tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@DisplayName("JSON mapper tests")
class JSONMapperTest {

	@Test
	@DisplayName("Should return same mapper instance for every context")
	void shouldReturnSameMapperInstanceForEveryContext() {
		// Given
		JSONMapper jsonMapper = new JSONMapper();
		// When
		ObjectMapper objectMapperA = jsonMapper.getContext(String.class);
		ObjectMapper objectMapperB = jsonMapper.getContext(Integer.class);
		// Then
		assertNotNull(objectMapperA);
		assertSame(objectMapperA, objectMapperB);
	}

	@Test
	@DisplayName("Should configure timestamp serialization and deserialization flags")
	void shouldConfigureTimestampSerializationAndDeserializationFlags() {
		// Given
		JSONMapper jsonMapper = new JSONMapper();
		ObjectMapper objectMapper = jsonMapper.getContext(Object.class);
		// Then
		assertNotNull(objectMapper);
		assertTrue(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
		assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS));
		assertFalse(objectMapper.isEnabled(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS));
		assertFalse(objectMapper.isEnabled(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE));
	}
}
