package fr.stp_ws.data.model;

import java.sql.Timestamp;
import java.util.List;

import fr.stp_ws.domain.model.miscellaneous.EntityType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * User placelist model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Table(name = "placelists_user")
public class PlacelistUser extends Placelist {

	// Placelist legacy

	/** Constructor */
	public PlacelistUser() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param owner
	 * @param type
	 * @param title
	 * @param rating
	 * @param description
	 * @param visibility
	 * @param creationDate
	 * @param lastUpdate
	 * @param places
	 * @param comments
	 */
	public PlacelistUser(Integer id, User owner, EntityType type, String title, Float rating, String description,
			Boolean visibility, Timestamp creationDate, Timestamp lastUpdate, List<Place> places,
			List<CommentPlacelist> comments) {
		super(id, owner, type, title, rating, description, visibility, creationDate, lastUpdate, places, comments);
	}
}
