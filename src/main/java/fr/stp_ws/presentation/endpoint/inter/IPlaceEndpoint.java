package fr.stp_ws.presentation.endpoint.inter;

import java.util.List;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.CountDTO;
import fr.stp_ws.domain.model.dto.resource.PhotoDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.presentation.validator.RequestValidator;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Place endpoints interface
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IPlaceEndpoint {

	/**
	 * Get all places (according to parameters)
	 *
	 * @param validator
	 * @param category
	 * @param mine
	 * @param type
	 * @param fromLat
	 * @param toLat
	 * @param fromLong
	 * @param toLong
	 * @return List<PlaceDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/list/{category}/{mine}/{type}/{fromLat}/{toLat}/{fromLong}/{toLong}")
	public List<PlaceDTO> getPlaces(@BeanParam RequestValidator validator, @PathParam("category") String category,
			@PathParam("mine") Boolean mine, @PathParam("type") String type, @PathParam("fromLat") Double fromLat,
			@PathParam("toLat") Double toLat, @PathParam("fromLong") Double fromLong,
			@PathParam("toLong") Double toLong) throws FunctionalException, TechnicalException;

	/**
	 * Get the place
	 *
	 * @param validator
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/{placeId:\\d+}")
	public PlaceDTO getPlace(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;

	/**
	 * Count owner places
	 *
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/count")
	public CountDTO countOwnerPlaces() throws FunctionalException, TechnicalException;

	/**
	 * Add the place
	 *
	 * @param validator
	 * @param placeDTO
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	public PlaceDTO addPlace(@BeanParam RequestValidator validator, PlaceDTO placeDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * Update the place
	 *
	 * @param validator
	 * @param placeDTO
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@PUT
	public PlaceDTO updatePlace(@BeanParam RequestValidator validator, PlaceDTO placeDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * Delete the place
	 *
	 * @param validator
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@DELETE
	@Path("/{placeId:\\d+}")
	public PlaceDTO deletePlace(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;

	/**
	 * Get all comments
	 *
	 * @param validator
	 * @param placeId
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/comments/list/{placeId:\\d+}")
	public List<CommentDTO> getComments(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;

	/**
	 * Count owner comment
	 *
	 * @param validator
	 * @param placeId
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/comments/count/{placeId:\\d+}")
	public CountDTO countOwnerComment(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;

	/**
	 * Add the comment
	 *
	 * @param validator
	 * @param placeId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/comment/{placeId:\\d+}")
	public CommentDTO addComment(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId,
			CommentDTO commentDTO) throws FunctionalException, TechnicalException;

	/**
	 * Delete the comment
	 *
	 * @param validator
	 * @param placeId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@DELETE
	@Path("/comment/{placeId:\\d+}")
	public CommentDTO deleteComment(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId,
			CommentDTO commentDTO) throws FunctionalException, TechnicalException;

	/**
	 * Get all photos
	 *
	 * @param validator
	 * @param placeId
	 * @return List<PhotoDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/photos/list/{placeId:\\d+}")
	public List<PhotoDTO> getPhotos(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;

	/**
	 * Count all photos
	 *
	 * @param validator
	 * @param placeId
	 * @return CountDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/photos/count/{placeId:\\d+}")
	public CountDTO countPhotos(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;

	/**
	 * Add the photo
	 *
	 * @param validator
	 * @param placeId
	 * @param photoDTO
	 * @return PhotoDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/photo/{placeId:\\d+}")
	public PhotoDTO addPhoto(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId,
			PhotoDTO photoDTO) throws FunctionalException, TechnicalException;

	/**
	 * Delete the photo
	 *
	 * @param validator
	 * @param placeId
	 * @param photoDTO
	 * @return PhotoDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@DELETE
	@Path("/photo/{placeId:\\d+}")
	public PhotoDTO deletePhoto(@BeanParam RequestValidator validator, @PathParam("placeId") Integer placeId,
			PhotoDTO photoDTO) throws FunctionalException, TechnicalException;
}
