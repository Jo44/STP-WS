package fr.stp_ws.data.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

/**
 * User model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Table(name = "users")
public class User extends fr.stp_ws.data.model.Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "name", length = 30, nullable = false)
	private String name;

	@Column(name = "email", length = 100, nullable = false)
	private String email;

	@Column(name = "tourist", nullable = false)
	private Boolean tourist;

	@Column(name = "google", nullable = false)
	private Boolean google;

	@Column(name = "secret", length = 100, nullable = false)
	private String secret;

	@Column(name = "refresh_token", length = 100, nullable = true)
	private String refreshToken;

	@Column(name = "refresh_token_expiry", nullable = true)
	private Timestamp refreshTokenExpiry;

	@Column(name = "creation_date", nullable = false)
	private Timestamp creationDate;

	@Column(name = "last_update", nullable = false)
	private Timestamp lastUpdate;

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Place> places = new ArrayList<Place>();

	@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Placelist> placelists = new ArrayList<Placelist>();

	@OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
	private List<Comment> comments = new ArrayList<Comment>();

	/** Constructor */
	public User() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param name
	 * @param email
	 * @param tourist
	 * @param google
	 * @param secret
	 * @param refreshToken
	 * @param refreshTokenExpiry
	 * @param creationDate
	 * @param lastUpdate
	 * @param placelists
	 * @param places
	 * @param comments
	 */
	public User(Integer id, String name, String email, Boolean tourist, Boolean google, String secret,
			String refreshToken, Timestamp refreshTokenExpiry, Timestamp creationDate, Timestamp lastUpdate,
			List<Placelist> placelists, List<Place> places, List<Comment> comments) {
		this();
		this.id = id;
		this.name = name;
		this.email = email;
		this.tourist = tourist;
		this.google = google;
		this.secret = secret;
		this.refreshToken = refreshToken;
		this.refreshTokenExpiry = refreshTokenExpiry;
		this.creationDate = creationDate;
		this.lastUpdate = lastUpdate;
		this.placelists = placelists;
		this.places = places;
		this.comments = comments;
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
		User that = (User) object;
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getTourist() {
		return tourist;
	}

	public void setTourist(Boolean tourist) {
		this.tourist = tourist;
	}

	public Boolean getGoogle() {
		return google;
	}

	public void setGoogle(Boolean google) {
		this.google = google;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public Timestamp getRefreshTokenExpiry() {
		return refreshTokenExpiry;
	}

	public void setRefreshTokenExpiry(Timestamp refreshTokenExpiry) {
		this.refreshTokenExpiry = refreshTokenExpiry;
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

	public List<Place> getPlaces() {
		return places;
	}

	public void setPlaces(List<Place> places) {
		this.places = places;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}
