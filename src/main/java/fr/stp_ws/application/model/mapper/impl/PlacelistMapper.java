package fr.stp_ws.application.model.mapper.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.IBasicPlacelistMapper;
import fr.stp_ws.application.model.mapper.inter.IPlacelistMapper;
import fr.stp_ws.application.service.inter.IPlacelistEnrichmentService;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.PlacelistTourist;
import fr.stp_ws.data.model.PlacelistUser;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.EntityCategory;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import jakarta.inject.Inject;

/**
 * Placelist mapper implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class PlacelistMapper implements IPlacelistMapper, IBasicPlacelistMapper {

	private static final Logger LOGGER = LogManager.getLogger(PlacelistMapper.class);
	private final IPlacelistEnrichmentService enrichmentService;

	/** Constructor */
	@Inject
	public PlacelistMapper(IPlacelistEnrichmentService enrichmentService) {
		this.enrichmentService = enrichmentService;
	}

	/**
	 * Convert placelist entity to placelist DTO
	 *
	 * @param placelist
	 * @param placelistMode
	 * @param commentMode
	 * @return PlacelistDTO
	 */
	@Override
	public PlacelistDTO toDTO(Placelist placelist, PlacelistMode placelistMode, CommentMode commentMode) {
		if (placelist == null) {
			return null;
		}
		LOGGER.debug("Converting placelist to DTO");
		PlacelistDTO placelistDTO = new PlacelistDTO();
		placelistDTO.setId(placelist.getId());
		placelistDTO.setType(placelist.getType());
		placelistDTO.setTitle(placelist.getTitle());
		placelistDTO.setRating(placelist.getRating());
		placelistDTO.setDescription(placelist.getDescription());
		placelistDTO.setVisibility(placelist.getVisibility());
		placelistDTO.setCreationDate(placelist.getCreationDate());
		placelistDTO.setLastUpdate(placelist.getLastUpdate());
		// Get owner
		try {
			placelistDTO.setOwner(placelist.getOwner().getName());
			placelistDTO.setCategory(placelist.getOwner().getTourist() ? EntityCategory.TOURIST : EntityCategory.USER);
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving owner : {}", ex.getMessage());
			placelistDTO.setOwner("User unknown");
			placelistDTO.setCategory(EntityCategory.USER);
		}
		// Enrich with places
		enrichmentService.enrichWithPlaces(placelist, placelistDTO, placelistMode);
		// Enrich with comments
		enrichmentService.enrichWithComments(placelist, placelistDTO, commentMode);
		return placelistDTO;
	}

	/**
	 * Convert placelist entity to placelist DTO (no comment / no place)
	 *
	 * @param placelist
	 * @return PlacelistDTO
	 */
	@Override
	public PlacelistDTO toBasicDTO(Placelist placelist) {
		if (placelist == null) {
			return null;
		}
		LOGGER.debug("Converting basic placelist to DTO");
		PlacelistDTO dto = new PlacelistDTO();
		dto.setId(placelist.getId());
		dto.setType(placelist.getType());
		dto.setTitle(placelist.getTitle());
		dto.setRating(placelist.getRating());
		dto.setDescription(placelist.getDescription());
		dto.setVisibility(placelist.getVisibility());
		dto.setCreationDate(placelist.getCreationDate());
		dto.setLastUpdate(placelist.getLastUpdate());
		// Get owner
		try {
			dto.setOwner(placelist.getOwner().getName());
			dto.setCategory(placelist.getOwner().getTourist() ? EntityCategory.TOURIST : EntityCategory.USER);
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving owner : {}", ex.getMessage());
			dto.setOwner("User unknown");
			dto.setCategory(EntityCategory.USER);
		}
		dto.setPlaces(new ArrayList<>());
		dto.setComments(new ArrayList<>());
		return dto;
	}

	/**
	 * Convert placelist entities to placelist DTOs
	 *
	 * @param placelists
	 * @param placelistMode
	 * @param commentMode
	 * @return List<PlacelistDTO>
	 */
	@Override
	public List<PlacelistDTO> toDTOList(List<Placelist> placelists, PlacelistMode placelistMode,
			CommentMode commentMode) {
		if (placelists == null) {
			return new ArrayList<>();
		}
		LOGGER.debug("Converting placelists to DTOs");
		return placelists.stream().map(placelist -> toDTO(placelist, placelistMode, commentMode))
				.collect(Collectors.toList());
	}

	/**
	 * Convert placelist entities to placelist DTOs (no comment / no place)
	 *
	 * @param placelists
	 * @return List<PlacelistDTO>
	 */
	@Override
	public List<PlacelistDTO> toBasicDTOList(List<Placelist> placelists) {
		if (placelists == null) {
			return new ArrayList<>();
		}
		LOGGER.debug("Converting basic placelists to DTOs");
		return placelists.stream().map(this::toBasicDTO).collect(Collectors.toList());
	}

	/**
	 * Convert placelist DTO to placelist entity
	 *
	 * @param placelistDTO
	 * @param user
	 * @return Placelist
	 */
	@Override
	public Placelist toEntity(PlacelistDTO placelistDTO, User user) {
		Placelist placelist;
		if (placelistDTO == null) {
			return null;
		}
		LOGGER.debug("Converting DTO to entity");
		if (!user.getTourist()) {
			PlacelistUser plUser = new PlacelistUser();
			plUser.setId(placelistDTO.getId());
			plUser.setOwner(user);
			plUser.setType(placelistDTO.getType());
			plUser.setTitle(placelistDTO.getTitle());
			plUser.setRating(0.0f);
			plUser.setDescription(placelistDTO.getDescription());
			plUser.setVisibility(placelistDTO.getVisibility());
			plUser.setCreationDate(placelistDTO.getCreationDate() != null ? placelistDTO.getCreationDate()
					: new Timestamp(System.currentTimeMillis()));
			plUser.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			plUser.setPlaces(new ArrayList<>());
			plUser.setComments(new ArrayList<>());
			placelist = plUser;
		} else {
			PlacelistTourist plTourist = new PlacelistTourist();
			plTourist.setId(placelistDTO.getId());
			plTourist.setOwner(user);
			plTourist.setType(placelistDTO.getType());
			plTourist.setTitle(placelistDTO.getTitle());
			plTourist.setRating(0.0f);
			plTourist.setDescription(placelistDTO.getDescription());
			plTourist.setVisibility(placelistDTO.getVisibility());
			plTourist.setCreationDate(placelistDTO.getCreationDate() != null ? placelistDTO.getCreationDate()
					: new Timestamp(System.currentTimeMillis()));
			plTourist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			plTourist.setPlaces(new ArrayList<>());
			plTourist.setComments(new ArrayList<>());
			placelist = plTourist;
		}
		return placelist;
	}
}
