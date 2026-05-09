package fr.stp_ws.application.model.mapper.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.IPhotoMapper;
import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;

/**
 * Photo mapper implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class PhotoMapper implements IPhotoMapper {

	private static final Logger LOGGER = LogManager.getLogger(PhotoMapper.class);

	/** Constructor */
	public PhotoMapper() {
		super();
	}

	/**
	 * Convert photo entity to photo DTO
	 *
	 * @param photo
	 * @return PhotoDTO
	 */
	@Override
	public PhotoDTO toDTO(Photo photo) {
		if (photo == null) {
			return null;
		}
		LOGGER.debug("Converting photo to DTO");
		PhotoDTO photoDTO = new PhotoDTO();
		photoDTO.setId(photo.getId());
		photoDTO.setUrl(photo.getUrl());
		photoDTO.setDescription(photo.getDescription());
		photoDTO.setUploadDate(photo.getUploadDate());
		// Get place
		try {
			photoDTO.setPlaceId(photo.getPlace().getId());
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving place : {}", ex.getMessage());
			photoDTO.setPlaceId(-1);
		}
		return photoDTO;
	}

	/**
	 * Convert photo entities to photo DTOs
	 *
	 * @param photos
	 * @return List<PhotoDTO>
	 */
	@Override
	public List<PhotoDTO> toDTOList(List<Photo> photos) {
		if (photos == null) {
			return new ArrayList<>();
		}
		LOGGER.debug("Converting photos to DTOs");
		return photos.stream().map(this::toDTO).collect(Collectors.toList());
	}

	/**
	 * Convert photo DTO to photo entity
	 *
	 * @param photoDTO
	 * @param place
	 * @return Photo
	 */
	@Override
	public Photo toEntity(PhotoDTO photoDTO, Place place) {
		if (photoDTO == null) {
			return null;
		}
		LOGGER.debug("Converting DTO to entity");
		Photo photo = new Photo();
		photo.setId(photoDTO.getId());
		photo.setPlace(place);
		photo.setUrl(photoDTO.getUrl());
		photo.setDescription(photoDTO.getDescription());
		photo.setUploadDate(new Timestamp(System.currentTimeMillis()));
		return photo;
	}
}
