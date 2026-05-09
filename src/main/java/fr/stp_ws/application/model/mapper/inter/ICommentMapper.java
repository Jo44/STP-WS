package fr.stp_ws.application.model.mapper.inter;

import java.util.List;

import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.Entity;
import fr.stp_ws.data.model.User;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;

/**
 * Comment mapper interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface ICommentMapper {

	/**
	 * Convert comment entity to comment DTO
	 *
	 * @param comment
	 * @return CommentDTO
	 */
	public CommentDTO toDTO(Comment comment);

	/**
	 * Convert comment entities to comment DTOs
	 *
	 * @param comments
	 * @return List<CommentDTO>
	 */
	public List<CommentDTO> toDTOList(List<Comment> comments);

	/**
	 * Convert comment DTO to comment entity
	 *
	 * @param commentDTO
	 * @param user
	 * @param entity
	 * @param type
	 * @return Comment
	 */
	public Comment toEntity(CommentDTO commentDTO, User user, Entity entity, Class<?> type);
}
