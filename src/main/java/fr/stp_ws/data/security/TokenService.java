package fr.stp_ws.data.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.security.ITokenService;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.exception.FunctionalException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Jwts.SIG;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.Keys;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Token service implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class TokenService implements ITokenService {

	private static final Logger LOGGER = LogManager.getLogger(TokenService.class);
	private final String stptKey;
	private final SecretKey signingStptKey;
	private final String jwtKey;
	private final SecretKey signingJwtKey;

	/**
	 * Constructor
	 *
	 * @param settings
	 */
	@Inject
	public TokenService(Settings settings) {
		// Load security keys from settings
		stptKey = settings.getString("auth.stpt.key");
		signingStptKey = Keys.hmacShaKeyFor(stptKey.getBytes(StandardCharsets.UTF_8));
		jwtKey = settings.getString("auth.jwt.key");
		signingJwtKey = Keys.hmacShaKeyFor(jwtKey.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generate STPT for user
	 *
	 * @param user
	 * @return String
	 * @throws FunctionalException
	 */
	@Override
	public String generateSTPT(User user) throws FunctionalException {
		if (user == null) {
			throw new FunctionalException("User cannot be null");
		}
		LOGGER.debug("Generating STPT");
		String stpt = null;
		try {
			Map<String, Object> claims = Jwts.claims().subject(String.valueOf(user.getId())).issuedAt(new Date())
					.expiration(new Date(System.currentTimeMillis() + 86400000)).add("name", user.getName())
					.add("email", user.getEmail()).build();
			stpt = Jwts.builder().claims(claims).signWith(signingStptKey, SIG.HS256).compact();
		} catch (InvalidKeyException ikex) {
			LOGGER.error("Error while generating STPT : {}", ikex.getMessage());
			throw new FunctionalException("Unable to generate STPT");
		}
		return stpt;
	}

	/**
	 * Generate JWT for user
	 *
	 * @param user
	 * @return String
	 * @throws FunctionalException
	 */
	@Override
	public String generateJWT(User user) throws FunctionalException {
		if (user == null) {
			throw new FunctionalException("User cannot be null");
		}
		LOGGER.debug("Generating JWT");
		String jwt = null;
		try {
			Map<String, Object> claims = Jwts.claims().subject(String.valueOf(user.getId())).issuedAt(new Date())
					.expiration(new Date(System.currentTimeMillis() + 3600000)).add("name", user.getName())
					.add("email", user.getEmail()).build();
			jwt = Jwts.builder().claims(claims).signWith(signingJwtKey, SIG.HS256).compact();
		} catch (InvalidKeyException ikex) {
			LOGGER.error("Error while generating JWT : {}", ikex.getMessage());
			throw new FunctionalException("Unable to generate JWT");
		}
		return jwt;
	}

	/**
	 * Get user ID from STPT
	 *
	 * @param stpt
	 * @return Integer
	 * @throws FunctionalException
	 */
	@Override
	public Integer getIDFromSTPT(String stpt) throws FunctionalException {
		LOGGER.debug("Retrieving user ID from STPT");
		Integer id;
		try {
			JwtParser parser = Jwts.parser().verifyWith(signingStptKey).build();
			Jwt<?, ?> jwtObj = parser.parse(stpt);
			Claims claims = (Claims) jwtObj.getPayload();
			String idStr = claims.getSubject();
			if (idStr == null || idStr.trim().isEmpty()) {
				throw new FunctionalException("Incorrect ID");
			} else {
				id = Integer.valueOf(idStr);
			}
		} catch (Exception ex) {
			LOGGER.error("Error while retrieving user ID from STPT : {}", ex.getMessage());
			throw new FunctionalException("Unable to get user ID from STPT");
		}
		return id;
	}

	/**
	 * Get user ID from JWT
	 *
	 * @param jwt
	 * @return Integer
	 * @throws FunctionalException
	 */
	@Override
	public Integer getIDFromJWT(String jwt) throws FunctionalException {
		LOGGER.debug("Retrieving user ID from JWT");
		Integer id;
		try {
			JwtParser parser = Jwts.parser().verifyWith(signingJwtKey).build();
			Jwt<?, ?> jwtObj = parser.parse(jwt);
			Claims claims = (Claims) jwtObj.getPayload();
			String idStr = claims.getSubject();
			if (idStr == null || idStr.trim().isEmpty()) {
				throw new FunctionalException("Incorrect ID");
			} else {
				id = Integer.valueOf(idStr);
			}
		} catch (Exception ex) {
			LOGGER.error("Error while retrieving user ID from JWT : {}", ex.getMessage());
			throw new FunctionalException("Unable to get user ID from JWT");
		}
		return id;
	}
}
