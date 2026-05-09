package fr.stp_ws.data.model;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Place comment model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Table(name = "comments_place")
public class CommentPlace extends Comment {

	// Comment legacy

	@ManyToOne
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	/** Constructor */
	public CommentPlace() {
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
	 * @param place
	 */
	public CommentPlace(Integer id, User owner, Float rating, String message, Timestamp creationDate,
			Timestamp lastUpdate, Place place) {
		super(id, owner, rating, message, creationDate, lastUpdate);
		this.place = place;
	}

	/* Getter / Setter */

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}
}
