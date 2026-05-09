package fr.stp_ws.data.model;

import java.sql.Timestamp;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Comment abstract model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "comments")
public abstract class Comment extends fr.stp_ws.data.model.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;

	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private User owner;

	@Column(name = "rating", nullable = false)
	private Float rating;

	@Column(name = "message", length = 1000, nullable = false)
	private String message;

	@Column(name = "creation_date", nullable = false)
	private Timestamp creationDate;

	@Column(name = "last_update", nullable = false)
	private Timestamp lastUpdate;

	/** Constructor */
	public Comment() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param owner
	 * @param rating
	 * @param message
	 * @param creationDate
	 * @param lastUpdate
	 */
	public Comment(Integer id, User owner, Float rating, String message, Timestamp creationDate, Timestamp lastUpdate) {
		this();
		this.id = id;
		this.owner = owner;
		this.rating = rating;
		this.message = message;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
	}

	/**
	 * Equals
	 */
	@Override
	public boolean equals(Object object) {
		if (this == object)
			return true;
		if (object == null || getClass() != object.getClass())
			return false;
		Comment that = (Comment) object;
		return id != null && id.equals(that.id);
	}

	/**
	 * Hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/* Getters / Setters */

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
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

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}
}
