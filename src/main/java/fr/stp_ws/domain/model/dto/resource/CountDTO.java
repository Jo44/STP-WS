package fr.stp_ws.domain.model.dto.resource;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Count DTO
 *
 * @author Jo44
 * @version 1.0 (12/05/2026)
 * @since 12/05/2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CountDTO {

	private Integer count;

	/** Constructor */
	public CountDTO() {
		super();
	}

	/**
	 * To String
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[ Count : ");
		sb.append(String.valueOf(count));
		sb.append(" ]");
		return sb.toString();
	}

	/* Getters / Setters */

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
}
