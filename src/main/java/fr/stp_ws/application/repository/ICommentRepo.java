package fr.stp_ws.application.repository;

import java.util.List;

import fr.stp_ws.data.model.Comment;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;

/**
 * Comment repository interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface ICommentRepo {

	/**
	 * Get all comments
	 *
	 * @param entityId
	 * @param owner
	 * @param type
	 * @return List<Comment>
	 * @throws TechnicalException
	 */
	public List<Comment> getAll(Integer entityId, Integer owner, Class<?> type) throws TechnicalException;

	/**
	 * Get a comment
	 *
	 * @param commentId
	 * @param type
	 * @return Comment
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Comment get(Integer commentId, Class<?> type) throws FunctionalException, TechnicalException;

	/**
	 * Add a comment
	 *
	 * @param entityId
	 * @param comment
	 * @param type
	 * @return Comment
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Comment add(Integer entityId, Comment comment, Class<?> type) throws FunctionalException, TechnicalException;

	/**
	 * Delete a comment
	 *
	 * @param commentId
	 * @param type
	 * @return Comment
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	public Comment delete(Integer commentId, Class<?> type) throws FunctionalException, TechnicalException;

	/**
	 * Check if the user has already commented
	 *
	 * @param entityId
	 * @param userId
	 * @param type
	 * @return Boolean
	 * @throws TechnicalException
	 */
	public Boolean hasCommented(Integer entityId, Integer userId, Class<?> type) throws TechnicalException;
}
