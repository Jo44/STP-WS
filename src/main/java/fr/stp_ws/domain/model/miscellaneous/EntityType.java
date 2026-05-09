package fr.stp_ws.domain.model.miscellaneous;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Entity type enumeration
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public enum EntityType {
	ALL("all"), CHILL("chill"), CULTURE("culture"), DRINK("drink"), EAT("eat"), FAMILY("family"), NATURE("nature"),
	SPORT("sport");

	private final String value;

	/**
	 * Constructor
	 *
	 * @param value
	 */
	EntityType(String value) {
		this.value = value;
	}

	/**
	 * Get value
	 *
	 * @return String
	 */
	@JsonValue
	public String getValue() {
		return value;
	}

	/**
	 * Get type from value
	 *
	 * @param value
	 * @return EntityType
	 */
	public static EntityType fromValue(String value) {
		EntityType type = EntityType.ALL;
		for (EntityType foundType : EntityType.values()) {
			if (foundType.value.equalsIgnoreCase(value)) {
				type = foundType;
			}
		}
		return type;
	}
}
