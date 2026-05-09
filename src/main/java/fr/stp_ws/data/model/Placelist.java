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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * Placelist abstract model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "placelists")
public abstract class Placelist extends fr.stp_ws.data.model.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private User owner;

	@Column(name = "type", nullable = false)
	private EntityType type;

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

	@ManyToMany
	@JoinTable(name = "placelists_places", joinColumns = @JoinColumn(name = "placelist_id"), inverseJoinColumns = @JoinColumn(name = "place_id"))
	private List<Place> places;

	@OneToMany(mappedBy = "placelist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CommentPlacelist> comments = new ArrayList<CommentPlacelist>();

	/** Constructor */
	public Placelist() {
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
	public Placelist(Integer id, User owner, EntityType type, String title, Float rating, String description,
			Boolean visibility, Timestamp creationDate, Timestamp lastUpdate, List<Place> places,
			List<CommentPlacelist> comments) {
		super();
		this.id = id;
		this.owner = owner;
		this.type = type;
		this.title = title;
		this.rating = rating;
		this.description = description;
		this.visibility = visibility;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
		this.places = places;
		this.comments = comments;
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

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public List<CommentPlacelist> getComments() {
		return comments;
	}

	public void setComments(List<CommentPlacelist> comments) {
		this.comments = comments;
	}
}
