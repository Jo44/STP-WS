package fr.stp_ws.application.model.mapper.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.IBasicPlaceMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.service.inter.IPlaceEnrichmentService;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.PlaceTourist;
import fr.stp_ws.data.model.PlaceUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import jakarta.inject.Inject;

/**
 * Place mapper implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class PlaceMapper implements IPlaceMapper, IBasicPlaceMapper {

	private static final Logger LOGGER = LogManager.getLogger(PlaceMapper.class);
	private final IPlaceEnrichmentService enrichmentService;

	/** Constructor */
	@Inject
	public PlaceMapper(IPlaceEnrichmentService enrichmentService) {
		this.enrichmentService = enrichmentService;
	}

	/**
	 * Convert place entity to place DTO
	 *
	 * @param place
	 * @param commentMode
	 * @param photoMode
	 * @return PlaceDTO
	 */
	@Override
	public PlaceDTO toDTO(Place place, CommentMode commentMode, PhotoMode photoMode) {
		if (place == null) {
			return null;
		}
		LOGGER.debug("Converting place to DTO");
		PlaceDTO placeDTO = new PlaceDTO();
		placeDTO.setId(place.getId());
		placeDTO.setType(place.getType());
		placeDTO.setTimes(place.getTimes());
		placeDTO.setLatitude(place.getLatitude());
		placeDTO.setLongitude(place.getLongitude());
		placeDTO.setTitle(place.getTitle());
		placeDTO.setRating(place.getRating());
		placeDTO.setDescription(place.getDescription());
		placeDTO.setVisibility(place.getVisibility());
		placeDTO.setCreationDate(place.getCreationDate());
		placeDTO.setLastUpdate(place.getLastUpdate());
		// Get owner
		try {
			placeDTO.setOwner(place.getOwner().getName());
			placeDTO.setCategory(place.getOwner().getTourist() ? EntityCategory.TOURIST : EntityCategory.USER);
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving owner : {}", ex.getMessage());
			placeDTO.setOwner("User unknown");
			placeDTO.setCategory(EntityCategory.USER);
		}
		// Enrich with comments
		enrichmentService.enrichWithComments(place, placeDTO, commentMode);
		// Enrich with photos
		enrichmentService.enrichWithPhotos(place, placeDTO, photoMode);
		return placeDTO;
	}

	/**
	 * Convert place entity to place DTO (no comment / no photo)
	 *
	 * @param place
	 * @return PlaceDTO
	 */
	@Override
	public PlaceDTO toBasicDTO(Place place) {
		if (place == null) {
			return null;
		}
		LOGGER.debug("Converting basic place to DTO");
		PlaceDTO placeDTO = new PlaceDTO();
		placeDTO.setId(place.getId());
		placeDTO.setType(place.getType());
		placeDTO.setTimes(place.getTimes());
		placeDTO.setLatitude(place.getLatitude());
		placeDTO.setLongitude(place.getLongitude());
		placeDTO.setTitle(place.getTitle());
		placeDTO.setRating(place.getRating());
		placeDTO.setDescription(place.getDescription());
		placeDTO.setVisibility(place.getVisibility());
		placeDTO.setCreationDate(place.getCreationDate());
		placeDTO.setLastUpdate(place.getLastUpdate());
		// Get owner
		try {
			placeDTO.setOwner(place.getOwner().getName());
			placeDTO.setCategory(place.getOwner().getTourist() ? EntityCategory.TOURIST : EntityCategory.USER);
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving owner : {}", ex.getMessage());
			placeDTO.setOwner("User unknown");
			placeDTO.setCategory(EntityCategory.USER);
		}
		placeDTO.setComments(new ArrayList<>());
		placeDTO.setPhotos(new ArrayList<>());
		return placeDTO;
	}

	/**
	 * Convert place entities to place DTOs
	 *
	 * @param places
	 * @param commentMode
	 * @param photoMode
	 * @return List<PlaceDTO>
	 */
	@Override
	public List<PlaceDTO> toDTOList(List<Place> places, CommentMode commentMode, PhotoMode photoMode) {
		if (places == null) {
			return new ArrayList<>();
		}
		LOGGER.debug("Converting places to DTOs");
		return places.stream().map(place -> toDTO(place, commentMode, photoMode)).collect(Collectors.toList());
	}

	/**
	 * Convert place entities to place DTOs (no comment / no photo)
	 *
	 * @param places
	 * @return List<PlaceDTO>
	 */
	@Override
	public List<PlaceDTO> toBasicDTOList(List<Place> places) {
		if (places == null) {
			return new ArrayList<>();
		}
		LOGGER.debug("Converting basic places to DTOs");
		return places.stream().map(this::toBasicDTO).collect(Collectors.toList());
	}

	/**
	 * Convert place DTO to place entity
	 *
	 * @param placeDTO
	 * @param user
	 * @return Place
	 */
	@Override
	public Place toEntity(PlaceDTO placeDTO, User user) {
		Place place;
		if (placeDTO == null) {
			return null;
		}
		LOGGER.debug("Converting DTO to entity");
		if (!user.getTourist()) {
			PlaceUser pUser = new PlaceUser();
			pUser.setId(placeDTO.getId());
			pUser.setOwner(user);
			pUser.setType(placeDTO.getType());
			pUser.setTimes(placeDTO.getTimes());
			pUser.setLatitude(placeDTO.getLatitude());
			pUser.setLongitude(placeDTO.getLongitude());
			pUser.setTitle(placeDTO.getTitle());
			pUser.setRating(0.0f);
			pUser.setDescription(placeDTO.getDescription());
			pUser.setVisibility(placeDTO.getVisibility());
			pUser.setCreationDate(placeDTO.getCreationDate() != null ? placeDTO.getCreationDate()
					: new Timestamp(System.currentTimeMillis()));
			pUser.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			pUser.setComments(new ArrayList<>());
			pUser.setPhotos(new ArrayList<>());
			place = pUser;
		} else {
			PlaceTourist pTourist = new PlaceTourist();
			pTourist.setId(placeDTO.getId());
			pTourist.setOwner(user);
			pTourist.setType(placeDTO.getType());
			pTourist.setTimes(placeDTO.getTimes());
			pTourist.setLatitude(placeDTO.getLatitude());
			pTourist.setLongitude(placeDTO.getLongitude());
			pTourist.setTitle(placeDTO.getTitle());
			pTourist.setRating(0.0f);
			pTourist.setDescription(placeDTO.getDescription());
			pTourist.setVisibility(placeDTO.getVisibility());
			pTourist.setCreationDate(placeDTO.getCreationDate() != null ? placeDTO.getCreationDate()
					: new Timestamp(System.currentTimeMillis()));
			pTourist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			pTourist.setComments(new ArrayList<>());
			pTourist.setPhotos(new ArrayList<>());
			place = pTourist;
		}
		return place;
	}
}
