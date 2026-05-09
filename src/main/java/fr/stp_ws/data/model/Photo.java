package fr.stp_ws.data.model;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Photo model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Table(name = "photos")
public class Photo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected Integer id;

	@Column(name = "url", length = 1000, nullable = false)
	private String url;

	@Column(name = "description", length = 500, nullable = true)
	private String description;

	@Column(name = "upload_date", nullable = false)
	private Timestamp uploadDate;

	@ManyToOne
	@JoinColumn(name = "place_id", nullable = false)
	private Place place;

	/** Constructor */
	public Photo() {
		super();
	}

	/**
	 * Constructor
	 *
	 * @param id
	 * @param url
	 * @param description
	 * @param uploadDate
	 * @param place
	 */
	public Photo(Integer id, String url, String description, Timestamp uploadDate, Place place) {
		this();
		this.id = id;
		this.url = url;
		this.description = description;
		this.uploadDate = uploadDate;
		this.place = place;
	}

	/* Getters / Setters */

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Timestamp uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}
}
