package fr.stp_ws.domain.model.dto.resource;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Photo DTO
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PhotoDTO {

	private Integer id;
	private Integer placeId;
	private String url;
	private String description;
	private Timestamp uploadDate;

	/** Constructor */
	public PhotoDTO() {
		super();
	}

	/**
	 * To String
	 *
	 * @return String
	 */
	@Override
	public String toString() {
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm")
				.withZone(ZoneId.systemDefault());
		StringBuilder sb = new StringBuilder();
		sb.append("[ ID : ");
		sb.append(String.valueOf(id));
		sb.append(" - Place ID : ");
		sb.append(String.valueOf(placeId));
		sb.append(" - URL : ");
		if (url != null) {
			sb.append(url);
		} else {
			sb.append("null");
		}
		sb.append(" - Description : ");
		if (description != null) {
			sb.append(description);
		} else {
			sb.append("null");
		}
		sb.append(" - Upload Date : ");
		if (uploadDate != null) {
			sb.append(dateFormatter.format(uploadDate.toInstant()));
		} else {
			sb.append("null");
		}
		sb.append(" ]");
		return sb.toString();
	}

	/* Getters / Setters */

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getPlaceId() {
		return placeId;
	}

	public void setPlaceId(Integer placeId) {
		this.placeId = placeId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Timestamp uploadDate) {
		this.uploadDate = uploadDate;
	}
}
