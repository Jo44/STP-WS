package fr.stp_ws.domain.model.dto.resource;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Comment DTO
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommentDTO {

	private Integer id;
	private String owner;
	private Float rating;
	private String message;
	private Timestamp creationDate;

	/** Constructor */
	public CommentDTO() {
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
		sb.append(" - Owner : ");
		if (owner != null) {
			sb.append(owner);
		} else {
			sb.append("null");
		}
		sb.append(" - Rating : ");
		sb.append(String.valueOf(rating));
		sb.append(" - Message : ");
		if (message != null) {
			sb.append(message);
		} else {
			sb.append("null");
		}
		sb.append(" - Creation Date : ");
		if (creationDate != null) {
			sb.append(dateFormatter.format(creationDate.toInstant()));
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

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}
}
