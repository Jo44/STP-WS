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
 * Bug report model
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Entity
@Table(name = "reports")
public class BugReport {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "description", length = 500, nullable = false)
	private String description;

	@Column(name = "log_name", length = 30, nullable = false)
	private String logName;

	@Column(name = "collect_date", nullable = false)
	private Timestamp collectDate;

	@ManyToOne
	@JoinColumn(name = "owner", nullable = false)
	private User user;

	/** Constructor */
	public BugReport() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param id
	 * @param description
	 * @param logName
	 * @param collectDate
	 * @param user
	 */
	public BugReport(Integer id, String description, String logName, Timestamp collectDate, User user) {
		this();
		this.id = id;
		this.description = description;
		this.logName = logName;
		this.collectDate = collectDate;
		this.user = user;
	}

	/* Getters / Setters */

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public Timestamp getCollectDate() {
		return collectDate;
	}

	public void setCollectDate(Timestamp collectDate) {
		this.collectDate = collectDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
