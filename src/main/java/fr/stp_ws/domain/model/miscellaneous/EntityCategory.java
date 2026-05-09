package fr.stp_ws.domain.model.miscellaneous;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Entity category enumeration
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public enum EntityCategory {
	ALL("all"), TOURIST("tourist"), USER("user");

	private final String value;

	/**
	 * Constructor
	 *
	 * @param value
	 */
	EntityCategory(String value) {
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
	 * Get category from value
	 *
	 * @param value
	 * @return EntityCategory
	 */
	public static EntityCategory fromValue(String value) {
		EntityCategory category = EntityCategory.USER;
		for (EntityCategory foundCategory : EntityCategory.values()) {
			if (foundCategory.value.equalsIgnoreCase(value)) {
				category = foundCategory;
			}
		}
		return category;
	}
}
