package fr.stp_ws.domain.model.dto.resource;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.EntityType;

/**
 * Placelist DTO
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlacelistDTO {

	private Integer id;
	private String owner;
	private EntityCategory category;
	private EntityType type;
	private String title;
	private Float rating;
	private String description;
	private Boolean visibility;
	private Timestamp creationDate;
	private Timestamp lastUpdate;
	private List<PlaceDTO> places;
	private List<CommentDTO> comments;

	/** Constructor */
	public PlacelistDTO() {
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
		sb.append(" - Category : ");
		if (category != null) {
			sb.append(category.getValue());
		} else {
			sb.append("null");
		}
		sb.append(" - Type : ");
		if (type != null) {
			sb.append(type.getValue());
		} else {
			sb.append("null");
		}
		sb.append(" - Title : ");
		if (title != null) {
			sb.append(title);
		} else {
			sb.append("null");
		}
		sb.append(" - Rating : ");
		sb.append(String.valueOf(rating));
		sb.append(" - Description : ");
		if (description != null) {
			sb.append(description);
		} else {
			sb.append("null");
		}
		sb.append(" - Visibility : ");
		sb.append(String.valueOf(visibility));
		sb.append(" - Creation Date : ");
		if (creationDate != null) {
			sb.append(dateFormatter.format(creationDate.toInstant()));
		} else {
			sb.append("null");
		}
		sb.append(" - Last Update : ");
		if (lastUpdate != null) {
			sb.append(dateFormatter.format(lastUpdate.toInstant()));
		} else {
			sb.append("null");
		}
		sb.append(" - Places : ");
		if (places != null) {
			sb.append(String.valueOf(places.size()));
		} else {
			sb.append("null");
		}
		sb.append(" - Comments : ");
		if (comments != null) {
			sb.append(String.valueOf(comments.size()));
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

	public EntityCategory getCategory() {
		return category;
	}

	public void setCategory(EntityCategory category) {
		this.category = category;
	}

	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Float getRating() {
		return rating;
	}

	public void setRating(Float rating) {
		this.rating = rating;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getVisibility() {
		return visibility;
	}

	public void setVisibility(Boolean visibility) {
		this.visibility = visibility;
	}

	public Timestamp getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public List<PlaceDTO> getPlaces() {
		return places;
	}

	public void setPlaces(List<PlaceDTO> places) {
		this.places = places;
	}

	public List<CommentDTO> getComments() {
		return comments;
	}

	public void setComments(List<CommentDTO> comments) {
		this.comments = comments;
	}
}
