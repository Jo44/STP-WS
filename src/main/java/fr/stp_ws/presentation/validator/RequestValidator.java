package fr.stp_ws.presentation.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.domain.model.dto.auth.LogInDTO;
import fr.stp_ws.domain.model.dto.auth.SignUpDTO;
import fr.stp_ws.domain.model.dto.auth.TokenDTO;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import jakarta.inject.Singleton;

/**
 * Request validator
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class RequestValidator {

	private static final Logger LOGGER = LogManager.getLogger(RequestValidator.class);

	/** Constructor */
	public RequestValidator() {
		super();
	}

	/**
	 * Check if the name is valid
	 *
	 * @param name
	 * @return Boolean
	 */
	private Boolean isValidName(String name) {
		LOGGER.debug("Checking if the name is valid");
		Boolean valid = true;
		// The name must contain between 3 and 30 characters
		if (name == null || name.trim().isEmpty() || name.trim().length() < 3 || name.trim().length() > 30) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Check if the email is valid
	 *
	 * @param email
	 * @return Boolean
	 */
	private Boolean isValidEmail(String email) {
		LOGGER.debug("Checking if the email is valid");
		Boolean valid;
		// The email must contain between 6 and 100 characters
		if (email == null || email.trim().isEmpty() || email.trim().length() < 6 || email.trim().length() > 100) {
			valid = false;
		} else {
			// The email must match the pattern of a valid email
			String emailPattern = "^[^@]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
			valid = email.matches(emailPattern);
		}
		return valid;
	}

	/**
	 * Check if the password is valid
	 *
	 * @param password
	 * @return Boolean
	 */
	private Boolean isValidPassword(String password) {
		LOGGER.debug("Checking if the password is valid");
		Boolean valid = true;
		// The password must contain between 1 and 100 characters
		if (password == null || password.trim().isEmpty() || password.trim().length() > 100) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Check if the Google ID token is valid
	 *
	 * @param googleIdToken
	 * @return Boolean
	 */
	private Boolean isValidGoogleIDToken(String googleIDToken) {
		LOGGER.debug("Checking if the Google ID token is valid");
		Boolean valid = true;
		// The Google ID token must not be empty
		if (googleIDToken == null || googleIDToken.trim().isEmpty()) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Check the sign-up request
	 *
	 * @param signUpDTO
	 * @return Boolean
	 */
	public Boolean checkRequestSignup(SignUpDTO signUpDTO) {
		LOGGER.debug("Checking the sign-up request");
		Boolean valid = true;
		if (signUpDTO == null) {
			valid = false;
		} else {
			String name = signUpDTO.getName();
			String email = signUpDTO.getEmail();
			String password = signUpDTO.getPassword();
			// Name, email and password must be valid
			if (!(isValidName(name) && isValidEmail(email) && isValidPassword(password))) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check the login request
	 *
	 * @param logInDTO
	 * @return Boolean
	 */
	public Boolean checkRequestLogin(LogInDTO logInDTO) {
		LOGGER.debug("Checking the login request");
		Boolean valid = true;
		if (logInDTO == null) {
			valid = false;
		} else {
			String email = logInDTO.getEmail();
			String password = logInDTO.getPassword();
			String googleIDToken = logInDTO.getGoogleToken();
			// Email and password, or Google ID token, must be valid
			if (!(isValidEmail(email) && isValidPassword(password)) && !isValidGoogleIDToken(googleIDToken)) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check the token request
	 *
	 * @param tokenDTO
	 * @return Boolean
	 */
	public Boolean checkRequestToken(TokenDTO tokenDTO) {
		LOGGER.debug("Checking the token request");
		Boolean valid = true;
		if (tokenDTO == null) {
			valid = false;
		} else {
			String tokenStr = tokenDTO.getToken();
			// The token must not be empty
			if (tokenStr == null || tokenStr.trim().isEmpty()) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check area search parameters
	 *
	 * @param fromLat
	 * @param toLat
	 * @param fromLong
	 * @param toLong
	 * @return Boolean
	 */
	public Boolean checkAreaSearch(Double fromLat, Double toLat, Double fromLong, Double toLong) {
		LOGGER.debug("Checking the parameters of the geographical positioning");
		boolean valid = true;
		if (fromLat == null || toLat == null || fromLong == null || toLong == null) {
			// Starting / ending latitudes and longitudes must be provided
			valid = false;
		} else {
			// Latitudes must be between -90.0 and 90.0
			if (fromLat < -90.0 || fromLat > 90.0) {
				valid = false;
			}
			if (toLat < -90.0 || toLat > 90.0) {
				valid = false;
			}
			// Longitudes must be between -180.0 and 180.0
			if (fromLong < -180.0 || fromLong > 180.0) {
				valid = false;
			}
			if (toLong < -180.0 || toLong > 180.0) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check the ID
	 *
	 * @param id
	 * @return Boolean
	 */
	public Boolean checkID(Integer id) {
		LOGGER.debug("Checking the ID");
		boolean valid = true;
		if (id == null) {
			valid = false;
		} else {
			// ID must be provided
			if (id < 1) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check the place
	 *
	 * @param place
	 * @return Boolean
	 */
	public Boolean checkPlace(PlaceDTO place) {
		LOGGER.debug("Checking the place");
		boolean valid = true;
		if (place == null) {
			valid = false;
		} else {
			// ID must be valid (if provided)
			if (place.getId() != null && place.getId() < 0) {
				valid = false;
			}
			// Type must be provided
			if (place.getType() == null) {
				valid = false;
			}
			// Times must contain between 1 and 30 characters
			if (place.getTimes() == null || place.getTimes().isEmpty() || place.getTimes().length() > 30) {
				valid = false;
			}
			// Latitude must be between -90.0 and 90.0
			if (place.getLatitude() == null || place.getLatitude() < -90.0 || place.getLatitude() > 90.0) {
				valid = false;
			}
			// Longitude must be between -180.0 and 180.0
			if (place.getLongitude() == null || place.getLongitude() < -180.0 || place.getLongitude() > 180.0) {
				valid = false;
			}
			// Title must contain between 1 and 30 characters
			if (place.getTitle() == null || place.getTitle().isEmpty() || place.getTitle().length() > 30) {
				valid = false;
			}
			// Description can contain up to 1000 characters
			if (place.getDescription() != null && place.getDescription().length() > 1000) {
				valid = false;
			}
			// Visibility must be provided
			if (place.getVisibility() == null) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check the placelist
	 *
	 * @param placelist
	 * @return Boolean
	 */
	public Boolean checkPlacelist(PlacelistDTO placelist) {
		LOGGER.debug("Checking the placelist");
		boolean valid = true;
		if (placelist == null) {
			valid = false;
		} else {
			// ID must be valid (if provided)
			if (placelist.getId() != null && placelist.getId() < 0) {
				valid = false;
			}
			// Type must be provided
			if (placelist.getType() == null) {
				valid = false;
			}
			// Title must contain between 1 and 30 characters
			if (placelist.getTitle() == null || placelist.getTitle().isEmpty() || placelist.getTitle().length() > 30) {
				valid = false;
			}
			// Description can contain up to 1000 characters
			if (placelist.getDescription() != null && placelist.getDescription().length() > 1000) {
				valid = false;
			}
			// Visibility must be provided
			if (placelist.getVisibility() == null) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check the comment
	 *
	 * @param comment
	 * @return Boolean
	 */
	public Boolean checkComment(CommentDTO comment) {
		LOGGER.debug("Checking the comment");
		boolean valid = true;
		if (comment == null) {
			valid = false;
		} else {
			// ID must be valid (if provided)
			if (comment.getId() != null && comment.getId() < 0) {
				valid = false;
			}
			// Rating must be between 0 and 5
			if (comment.getRating() == null || comment.getRating() < 0f || comment.getRating() > 5f) {
				valid = false;
			}
			// Message must contain between 1 and 1000 characters
			if (comment.getMessage() == null || comment.getMessage().isEmpty()
					|| comment.getMessage().length() > 1000) {
				valid = false;
			}
		}
		return valid;
	}

	/**
	 * Check the photo
	 *
	 * @param photo
	 * @return Boolean
	 */
	public Boolean checkPhoto(PhotoDTO photo) {
		LOGGER.debug("Checking the photo");
		boolean valid = true;
		if (photo == null) {
			valid = false;
		} else {
			// ID must be valid (if provided)
			if (photo.getId() != null && photo.getId() < 0) {
				valid = false;
			}
			// URL must contain between 1 and 1000 characters
			if (photo.getUrl() == null || photo.getUrl().isEmpty() || photo.getUrl().length() > 1000) {
				valid = false;
			}
			// Description can contain up to 500 characters
			if (photo.getDescription() == null || photo.getDescription().length() > 500) {
				valid = false;
			}
		}
		return valid;
	}
}
