package fr.stp_ws.data.model;

import java.sql.Timestamp;
import java.util.List;

import fr.stp_ws.domain.model.miscellaneous.EntityType;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * User place model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Table(name = "places_user")
public class PlaceUser extends Place {

	// Place legacy

	/** Constructor */
	public PlaceUser() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param owner
	 * @param type
	 * @param times
	 * @param latitude
	 * @param longitude
	 * @param title
	 * @param rating
	 * @param description
	 * @param visibility
	 * @param creationDate
	 * @param lastUpdate
	 * @param placelists
	 * @param comments
	 * @param photos
	 */
	public PlaceUser(Integer id, User owner, EntityType type, String times, Double latitude, Double longitude,
			String title, Float rating, String description, Boolean visibility, Timestamp creationDate,
			Timestamp lastUpdate, List<Placelist> placelists, List<CommentPlace> comments, List<Photo> photos) {
		super(id, owner, type, times, latitude, longitude, title, rating, description, visibility, creationDate,
				lastUpdate, placelists, comments, photos);
	}
}
