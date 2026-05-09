package fr.stp_ws.data.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.query.Query;

import fr.stp_ws.application.repository.ICommentRepo;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.model.Comment;
import fr.stp_ws.data.model.CommentPlace;
import fr.stp_ws.data.model.CommentPlacelist;
import fr.stp_ws.data.model.Place;
import fr.stp_ws.data.model.Placelist;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.NotExistCommentException;
import fr.stp_ws.domain.exception.NotExistPlaceException;
import fr.stp_ws.domain.exception.NotExistPlacelistException;
import fr.stp_ws.domain.exception.TechnicalException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.PersistenceException;

/**
 * Comment repository implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class CommentRepo implements ICommentRepo {

	private static final Logger LOGGER = LogManager.getLogger(CommentRepo.class);
	@Inject
	private LoadingService loadingService;
	private final Hibernate hibernate;
	private final String placeGetAll;
	private final String placelistGetAll;
	private final String placeCount;
	private final String placelistCount;
	private final String placeRatingAvg;
	private final String placelistRatingAvg;

	/**
	 * Constructor
	 *
	 * @param hibernate
	 * @param settings
	 */
	@Inject
	public CommentRepo(Hibernate hibernate, Settings settings) {
		this.hibernate = hibernate;
		// Load SQL requests from settings
		placeGetAll = settings.getString("sql.com.pl.get.all");
		placelistGetAll = settings.getString("sql.com.pll.get.all");
		placeCount = settings.getString("sql.com.pl.count");
		placelistCount = settings.getString("sql.com.pll.count");
		placeRatingAvg = settings.getString("sql.com.pl.rating.avg");
		placelistRatingAvg = settings.getString("sql.com.pll.rating.avg");
	}

	/* Comment - Get */

	/**
	 * Get all comments
	 *
	 * @param entityId
	 * @param owner
	 * @param type
	 * @return List<Comment>
	 * @throws TechnicalException
	 */
	@Override
	public List<Comment> getAll(Integer entityId, Integer owner, Class<?> type) throws TechnicalException {
		List<Comment> comments = new ArrayList<>();
		Session session = hibernate.openSession();
		try {
			// Determine SQL request to use
			String queryString = isPlace(type) ? placeGetAll : placelistGetAll;
			// Determine return type
			Class<? extends Comment> returnType = isPlace(type) ? CommentPlace.class : CommentPlacelist.class;
			// Create query
			@SuppressWarnings("unchecked")
			Query<Comment> query = (Query<Comment>) session.createQuery(queryString, returnType);
			query.setParameter("id", entityId);
			query.setParameter("owner", owner);
			query.setMaxResults(10000);
			// Retrieve comments
			List<Comment> commentsResult = query.getResultList();
			// Load associations for each comment
			for (Comment comment : commentsResult) {
				loadingService.loadCommentAssociations(comment);
			}
			// Add comments
			comments.addAll(commentsResult);
			// Sort comments
			comments.sort(Comparator.comparing(Comment::getCreationDate).reversed());
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Comments retrieved successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving comments : {}", pex.getMessage());
			throw new TechnicalException("Unable to get comments");
		}
		return comments;
	}

	/**
	 * Get a comment
	 *
	 * @param commentId
	 * @param type
	 * @return Comment
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Comment get(Integer commentId, Class<?> type) throws FunctionalException, TechnicalException {
		Comment comment = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve comment
			comment = isPlace(type) ? session.find(CommentPlace.class, commentId)
					: session.find(CommentPlacelist.class, commentId);
			if (comment == null) {
				throw new NotExistCommentException("Comment does not exist");
			}
			// Load associations of comment
			loadingService.loadCommentAssociations(comment);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Comment retrieved successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving comment : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while retrieving comment : {}", pex.getMessage());
			throw new TechnicalException("Unable to get comment");
		}
		return comment;
	}

	/* Comment - Add / Delete */

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
	@Override
	public Comment add(Integer entityId, Comment comment, Class<?> type)
			throws FunctionalException, TechnicalException {
		Session session = hibernate.openSession();
		try {
			// Retrieve entity
			Object entity = isPlace(type) ? session.find(Place.class, entityId)
					: session.find(Placelist.class, entityId);
			if (entity == null) {
				if (isPlace(type)) {
					throw new NotExistPlaceException("Place does not exist");
				} else {
					throw new NotExistPlacelistException("Placelist does not exist");
				}
			}
			// Save comment
			session.persist(comment);
			// Calculate new average rating
			Float newRating = calculateRating(entityId, comment.getRating(), type, true);
			// Update average rating of entity
			if (isPlace(type)) {
				((Place) entity).setRating(newRating);
			} else {
				((Placelist) entity).setRating(newRating);
			}
			// Save entity
			session.merge(entity);
			// Load associations of comment
			loadingService.loadCommentAssociations(comment);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Comment added successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding comment : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while adding comment : {}", pex.getMessage());
			throw new TechnicalException("Unable to add comment");
		}
		return comment;
	}

	/**
	 * Delete a comment
	 *
	 * @param commentId
	 * @param type
	 * @return Comment
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@Override
	public Comment delete(Integer commentId, Class<?> type) throws FunctionalException, TechnicalException {
		Comment deletedComment = null;
		Session session = hibernate.openSession();
		try {
			// Retrieve comment
			Comment comment = isPlace(type) ? session.find(CommentPlace.class, commentId)
					: session.find(CommentPlacelist.class, commentId);
			if (comment == null) {
				throw new NotExistCommentException("Comment does not exist");
			}
			// Retrieve associated entity
			Object entity = isPlace(type) ? ((CommentPlace) comment).getPlace()
					: ((CommentPlacelist) comment).getPlacelist();
			// Delete comment
			deletedComment = comment;
			session.remove(comment);
			// Calculate new average rating
			Float newRating = calculateRating(isPlace(type) ? ((Place) entity).getId() : ((Placelist) entity).getId(),
					comment.getRating(), type, false);
			// Update average rating of entity
			if (isPlace(type)) {
				((Place) entity).setRating(newRating);
			} else {
				((Placelist) entity).setRating(newRating);
			}
			// Save entity
			session.merge(entity);
			// Load associations of comment
			loadingService.loadCommentAssociations(deletedComment);
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Comment deleted successfully");
		} catch (FunctionalException fex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting comment : {}", fex.getMessage());
			throw fex;
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while deleting comment : {}", pex.getMessage());
			throw new TechnicalException("Unable to delete comment");
		}
		return deletedComment;
	}

	/* Comment - Count */

	/**
	 * Check if the user has already commented the entity
	 *
	 * @param entityId
	 * @param userId
	 * @param type
	 * @return Boolean
	 * @throws TechnicalException
	 */
	@Override
	public Boolean hasCommented(Integer entityId, Integer userId, Class<?> type) throws TechnicalException {
		Boolean hasCommented = true;
		Session session = hibernate.openSession();
		try {
			// Determine SQL request to use
			Query<Long> query;
			if (isPlace(type)) {
				query = session.createQuery(placeCount, Long.class);
				query.setParameter("placeId", entityId);
			} else {
				query = session.createQuery(placelistCount, Long.class);
				query.setParameter("placelistId", entityId);
			}
			query.setParameter("userId", userId);
			// Retrieve comment count
			Long count = query.getSingleResult();
			// Determine if the user has already commented
			hasCommented = count > 0;
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Checked if user has already commented successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while checking if user has already commented : {}", pex.getMessage());
			throw new TechnicalException("Unable to check if user has already commented");
		}
		return hasCommented;
	}

	/* Comment - Rating */

	/**
	 * Calculate the new average rating of the entity after adding or deleting a
	 * comment
	 *
	 * @param entityId
	 * @param rating
	 * @param type
	 * @param isAdd
	 * @return Float
	 * @throws TechnicalException
	 */
	private Float calculateRating(Integer entityId, Float rating, Class<?> type, boolean isAdd)
			throws TechnicalException {
		Float newRating = 0.0f;
		Session session = hibernate.openSession();
		try {
			// Retrieve comment count and average rating of entity
			String queryString = isPlace(type) ? placeRatingAvg : placelistRatingAvg;
			Query<Object[]> query = session.createQuery(queryString, Object[].class);
			query.setParameter(isPlace(type) ? "placeId" : "placelistId", entityId);
			Object[] result = query.getSingleResult();
			// Extract count and average rating
			Long count = (Long) result[0];
			Double avgRating = (Double) result[1];
			// Calculate new average rating
			if (isAdd) {
				// When adding a comment
				if (count == 0) {
					// If first comment, the average rating becomes the rating of the comment
					newRating = rating;
				} else {
					// Otherwise, calculate the new average rating
					newRating = (float) ((avgRating * count + rating) / (count + 1));
				}
			} else {
				// When deleting a comment
				if (count <= 1) {
					// If it was the only comment, the average rating becomes 0
					newRating = 0.0f;
				} else {
					// Otherwise, calculate the new average rating
					newRating = (float) ((avgRating * count - rating) / (count - 1));
				}
			}
			// Commit
			hibernate.commit(session);
			LOGGER.debug("Calculated new average rating successfully");
		} catch (PersistenceException pex) {
			hibernate.rollback(session);
			LOGGER.error("Error while calculating new average rating : {}", pex.getMessage());
			throw new TechnicalException("Unable to calculate new average rating");
		}
		return newRating;
	}

	/**
	 * Check if the type is Place
	 *
	 * @param type
	 * @return Boolean
	 */
	private boolean isPlace(Class<?> type) {
		return type == Place.class;
	}
}
