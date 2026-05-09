package fr.stp_ws.application.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.IPhotoMapper;
import fr.stp_ws.application.service.inter.IPlaceEnrichmentService;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.miscellaneous.mode.CommentMode;
import fr.stp_ws.domain.model.miscellaneous.mode.PhotoMode;
import jakarta.inject.Inject;

/**
 * Place enrichment service implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class PlaceEnrichmentService implements IPlaceEnrichmentService {

	private static final Logger LOGGER = LogManager.getLogger(PlaceEnrichmentService.class);
	private final ICommentMapper commentMapper;
	private final IPhotoMapper photoMapper;

	/** Constructor */
	@Inject
	public PlaceEnrichmentService(ICommentMapper commentMapper, IPhotoMapper photoMapper) {
		this.commentMapper = commentMapper;
		this.photoMapper = photoMapper;
	}

	/**
	 * Enrich place DTO with comments (according to the specified mode)
	 *
	 * @param place
	 * @param placeDTO
	 * @param commentMode
	 */
	@Override
	public void enrichWithComments(Place place, PlaceDTO placeDTO, CommentMode commentMode) {
		LOGGER.debug("Enriching place with comments");
		try {
			if (commentMode != CommentMode.NONE && place.getComments() != null) {
				placeDTO.setComments(getTopComments(place.getComments(), 3));
			} else {
				placeDTO.setComments(new ArrayList<>());
			}
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving comments : {}", ex.getMessage());
			placeDTO.setComments(new ArrayList<>());
		}
	}

	/**
	 * Enrich place DTO with photos (according to the specified mode)
	 *
	 * @param place
	 * @param placeDTO
	 * @param photoMode
	 */
	@Override
	public void enrichWithPhotos(Place place, PlaceDTO placeDTO, PhotoMode photoMode) {
		LOGGER.debug("Enriching place with photos");
		try {
			if (photoMode != PhotoMode.NONE && place.getPhotos() != null) {
				placeDTO.setPhotos(getTopPhotos(place.getPhotos(), 3));
			} else {
				placeDTO.setPhotos(new ArrayList<>());
			}
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving photos : {}", ex.getMessage());
			placeDTO.setPhotos(new ArrayList<>());
		}
	}

	/**
	 * Get X most recent comments from a place
	 *
	 * @param comments
	 * @param limit
	 * @return List<CommentDTO>
	 */
	@Override
	public List<CommentDTO> getTopComments(List<CommentPlace> comments, int limit) {
		if (comments == null || comments.isEmpty()) {
			return new ArrayList<>();
		}
		return comments.stream().sorted(Comparator.comparing(CommentPlace::getCreationDate).reversed()).limit(limit)
				.map(comment -> commentMapper.toDTO(comment)).collect(Collectors.toList());
	}

	/**
	 * Get X most recent photos from a place
	 *
	 * @param photos
	 * @param limit
	 * @return List<PhotoDTO>
	 */
	@Override
	public List<PhotoDTO> getTopPhotos(List<Photo> photos, int limit) {
		if (photos == null || photos.isEmpty()) {
			return new ArrayList<>();
		}
		return photos.stream().sorted(Comparator.comparing(Photo::getUploadDate).reversed()).limit(limit)
				.map(photo -> photoMapper.toDTO(photo)).collect(Collectors.toList());
	}
}
