package fr.stp_ws.application.model.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import fr.stp_ws.application.model.mapper.impl.CountMapper;
import fr.stp_ws.domain.model.dto.resource.CountDTO;

/**
 * Count mapper tests
 *
 * @author Jo44
 * @version 1.0 (12/05/2026)
 * @since 12/05/2026
 */
@DisplayName("Count mapper tests")
class CountMapperTest {

	private CountMapper countMapper;

	/** Before each test */
	@BeforeEach
	void setUp() {
		countMapper = new CountMapper();
	}

	/** Convert count to DTO tests */
	@Nested
	@DisplayName("Convert count to DTO tests")
	class ToDTOTests {

		@Test
		@DisplayName("Should return null when count is null")
		void shouldReturnNullWhenCountIsNull() {
			assertNull(countMapper.toDTO(null));
		}

		@Test
		@DisplayName("Should map positive count to CountDTO")
		void shouldMapPositiveCountToCountDTO() {
			CountDTO dto = countMapper.toDTO(12);
			assertNotNull(dto);
			assertEquals(12, dto.getCount());
		}

		@Test
		@DisplayName("Should map zero count to CountDTO")
		void shouldMapZeroCountToCountDTO() {
			CountDTO dto = countMapper.toDTO(0);
			assertNotNull(dto);
			assertEquals(0, dto.getCount());
		}
	}
}
