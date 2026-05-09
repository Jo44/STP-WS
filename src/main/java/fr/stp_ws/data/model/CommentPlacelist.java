package fr.stp_ws.data.model;

import java.sql.Timestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Placelist comment model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Table(name = "comments_placelist")
public class CommentPlacelist extends Comment {

	// Comment legacy

	@ManyToOne
	@JoinColumn(name = "placelist_id", nullable = false)
	private Placelist placelist;

	/** Constructor */
	public CommentPlacelist() {
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
	 * @param placelist
	 */
	public CommentPlacelist(Integer id, User owner, Float rating, String message, Timestamp creationDate,
			Timestamp lastUpdate, Placelist placelist) {
		super(id, owner, rating, message, creationDate, lastUpdate);
		this.placelist = placelist;
	}

	/* Getter / Setter */

	public Placelist getPlacelist() {
		return placelist;
	}

	public void setPlacelist(Placelist placelist) {
		this.placelist = placelist;
	}
}
