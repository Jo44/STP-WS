package fr.stp_ws.application.model.mapper.inter;

import java.util.List;

import fr.stp_ws.data.model.Photo;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;

/**
 * Photo mapper interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface IPhotoMapper {

	/**
	 * Convert photo entity to photo DTO
	 *
	 * @param photo
	 * @return PhotoDTO
	 */
	public PhotoDTO toDTO(Photo photo);

	/**
	 * Convert photo entities to photo DTOs
	 *
	 * @param photos
	 * @return List<PhotoDTO>
	 */
	public List<PhotoDTO> toDTOList(List<Photo> photos);

	/**
	 * Convert photo DTO to photo entity
	 *
	 * @param photoDTO
	 * @param place
	 * @return Photo
	 */
	public Photo toEntity(PhotoDTO photoDTO, Place place);
}
