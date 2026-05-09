package fr.stp_ws.application.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.service.inter.IPlacelistEnrichmentService;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PlacelistMode;
import jakarta.inject.Inject;

/**
 * Placelist enrichment service implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class PlacelistEnrichmentService implements IPlacelistEnrichmentService {

	private static final Logger LOGGER = LogManager.getLogger(PlacelistEnrichmentService.class);
	private final IPlaceMapper placeMapper;
	private final ICommentMapper commentMapper;

	/** Constructor */
	@Inject
	public PlacelistEnrichmentService(IPlaceMapper placeMapper, ICommentMapper commentMapper) {
		this.placeMapper = placeMapper;
		this.commentMapper = commentMapper;
	}

	/**
	 * Enrich placelist DTO with comments (according to the specified mode)
	 *
	 * @param placelist
	 * @param placelistDTO
	 * @param commentMode
	 */
	@Override
	public void enrichWithComments(Placelist placelist, PlacelistDTO placelistDTO, CommentMode commentMode) {
		LOGGER.debug("Enriching placelist with comments");
		try {
			if (commentMode != CommentMode.NONE && placelist.getComments() != null) {
				placelistDTO.setComments(getTopComments(placelist.getComments(), 3));
			} else {
				placelistDTO.setComments(new ArrayList<>());
			}
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving comments : {}", ex.getMessage());
			placelistDTO.setComments(new ArrayList<>());
		}
	}

	/**
	 * Enrich placelist DTO with places (according to the specified mode)
	 *
	 * @param placelist
	 * @param placelistDTO
	 * @param placelistMode
	 */
	@Override
	public void enrichWithPlaces(Placelist placelist, PlacelistDTO placelistDTO, PlacelistMode placelistMode) {
		LOGGER.debug("Enriching placelist with places");
		try {
			if (placelistMode == PlacelistMode.WITH_PLACES && placelist.getPlaces() != null) {
				placelistDTO.setPlaces(placeMapper.toDTOList(placelist.getPlaces(), CommentMode.NONE, PhotoMode.NONE));
			} else {
				placelistDTO.setPlaces(new ArrayList<>());
			}
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving places : {}", ex.getMessage());
			placelistDTO.setPlaces(new ArrayList<>());
		}
	}

	/**
	 * Get X most recent comments from a placelist
	 *
	 * @param comments
	 * @param limit
	 * @return List<CommentDTO>
	 */
	@Override
	public List<CommentDTO> getTopComments(List<CommentPlacelist> comments, int limit) {
		if (comments == null || comments.isEmpty()) {
			return new ArrayList<>();
		}
		return comments.stream().sorted(Comparator.comparing(CommentPlacelist::getCreationDate).reversed()).limit(limit)
				.map(comment -> commentMapper.toDTO(comment)).collect(Collectors.toList());
	}
}
