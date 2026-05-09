package fr.stp_ws.data.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import fr.stp_ws.domain.model.miscellaneous.EntityType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Place abstract model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "places")
public abstract class Place extends fr.stp_ws.data.model.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private User owner;

	@Column(name = "type", nullable = false)
	private EntityType type;

	@Column(name = "times", length = 30, nullable = false)
	private String times;

	@Column(name = "latitude", nullable = false)
	private Double latitude;

	@Column(name = "longitude", nullable = false)
	private Double longitude;

	@Column(name = "title", length = 30, nullable = false)
	private String title;

	@Column(name = "rating", nullable = true)
	private Float rating;

	@Column(name = "description", length = 1000, nullable = true)
	private String description;

	@Column(name = "visibility", nullable = false)
	private Boolean visibility;

	@Column(name = "creation_date", nullable = false)
	private Timestamp creationDate;

	@Column(name = "last_update", nullable = false)
	private Timestamp lastUpdate;

	@ManyToMany(mappedBy = "places", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<Placelist> placelists;

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CommentPlace> comments = new ArrayList<CommentPlace>();

	@OneToMany(mappedBy = "place", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Photo> photos = new ArrayList<Photo>();

	/** Constructor */
	public Place() {
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
	public Place(Integer id, User owner, EntityType type, String times, Double latitude, Double longitude, String title,
			Float rating, String description, Boolean visibility, Timestamp creationDate, Timestamp lastUpdate,
			List<Placelist> placelists, List<CommentPlace> comments, List<Photo> photos) {
		super();
		this.id = id;
		this.owner = owner;
		this.type = type;
		this.times = times;
		this.latitude = latitude;
		this.longitude = longitude;
		this.title = title;
		this.rating = rating;
		this.description = description;
		this.visibility = visibility;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
		this.placelists = placelists;
		this.comments = comments;
		this.photos = photos;
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

	public EntityType getType() {
		return type;
	}

	public void setType(EntityType type) {
		this.type = type;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
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

	public List<Placelist> getPlacelists() {
		return placelists;
	}

	public void setPlacelists(List<Placelist> placelists) {
		this.placelists = placelists;
	}

	public List<CommentPlace> getComments() {
		return comments;
	}

	public void setComments(List<CommentPlace> comments) {
		this.comments = comments;
	}

	public List<Photo> getPhotos() {
		return photos;
	}

	public void setPhotos(List<Photo> photos) {
		this.photos = photos;
	}
}
