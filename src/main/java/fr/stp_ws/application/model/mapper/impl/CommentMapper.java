package fr.stp_ws.application.model.mapper.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Entity;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;

/**
 * Comment mapper implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public class CommentMapper implements ICommentMapper {

	private static final Logger LOGGER = LogManager.getLogger(CommentMapper.class);

	/** Constructor */
	public CommentMapper() {
		super();
	}

	/**
	 * Convert comment entity to comment DTO
	 *
	 * @param comment
	 * @return CommentDTO
	 */
	@Override
	public CommentDTO toDTO(Comment comment) {
		if (comment == null) {
			return null;
		}
		LOGGER.debug("Converting comment to DTO");
		CommentDTO commentDTO = new CommentDTO();
		commentDTO.setId(comment.getId());
		commentDTO.setRating(comment.getRating());
		commentDTO.setMessage(comment.getMessage());
		commentDTO.setCreationDate(comment.getCreationDate());
		// Get owner
		try {
			commentDTO.setOwner(comment.getOwner().getName());
		} catch (RuntimeException ex) {
			LOGGER.error("Error while retrieving owner : {}", ex.getMessage());
			commentDTO.setOwner("User unknown");
		}
		return commentDTO;
	}

	/**
	 * Convert comment entities to comment DTOs
	 *
	 * @param comments
	 * @return List<CommentDTO>
	 */
	@Override
	public List<CommentDTO> toDTOList(List<Comment> comments) {
		if (comments == null) {
			return new ArrayList<>();
		}
		LOGGER.debug("Converting comments to DTOs");
		return comments.stream().map(this::toDTO).collect(Collectors.toList());
	}

	/**
	 * Convert comment DTO to comment entity
	 *
	 * @param commentDTO
	 * @param user
	 * @param entity
	 * @param type
	 * @return Comment
	 */
	@Override
	public Comment toEntity(CommentDTO commentDTO, User user, Entity entity, Class<?> type) {
		Comment comment;
		if (commentDTO == null) {
			return null;
		}
		LOGGER.debug("Converting DTO to entity");
		if (type == Place.class) {
			CommentPlace commentPlace = new CommentPlace();
			commentPlace.setId(commentDTO.getId());
			commentPlace.setOwner(user);
			commentPlace.setRating(commentDTO.getRating());
			commentPlace.setMessage(commentDTO.getMessage());
			commentPlace.setCreationDate(commentDTO.getCreationDate() != null ? commentDTO.getCreationDate()
					: new Timestamp(System.currentTimeMillis()));
			commentPlace.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			commentPlace.setPlace((Place) entity);
			comment = commentPlace;
		} else {
			CommentPlacelist commentPlacelist = new CommentPlacelist();
			commentPlacelist.setId(commentDTO.getId());
			commentPlacelist.setOwner(user);
			commentPlacelist.setRating(commentDTO.getRating());
			commentPlacelist.setMessage(commentDTO.getMessage());
			commentPlacelist.setCreationDate(commentDTO.getCreationDate() != null ? commentDTO.getCreationDate()
					: new Timestamp(System.currentTimeMillis()));
			commentPlacelist.setLastUpdate(new Timestamp(System.currentTimeMillis()));
			commentPlacelist.setPlacelist((Placelist) entity);
			comment = commentPlacelist;
		}
		return comment;
	}
}
