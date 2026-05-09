package fr.stp_ws.presentation.endpoint.inter;

import java.util.List;

import fr.stp_ws.domain.exception.FunctionalException;
import fr.stp_ws.domain.exception.TechnicalException;
import fr.stp_ws.domain.model.dto.resource.CommentDTO;
import fr.stp_ws.domain.model.dto.resource.PlaceDTO;
import fr.stp_ws.domain.model.dto.resource.PlacelistDTO;
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
 * Placelist endpoints interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface IPlacelistEndpoint {

	/**
	 * Get all placelists (according to parameters)
	 *
	 * @param category
	 * @param mine
	 * @param type
	 * @return List<PlacelistDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/list/{category}/{mine}/{type}")
	public List<PlacelistDTO> getPlacelists(@PathParam("category") String category, @PathParam("mine") Boolean mine,
			@PathParam("type") String type) throws FunctionalException, TechnicalException;

	/**
	 * Get the placelist
	 *
	 * @param validator
	 * @param placelistId
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/{placelistId}")
	public PlacelistDTO getPlacelist(@BeanParam RequestValidator validator,
			@PathParam("placelistId") Integer placelistId) throws FunctionalException, TechnicalException;

	/**
	 * Add the placelist
	 *
	 * @param validator
	 * @param placelistDTO
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	public PlacelistDTO addPlacelist(@BeanParam RequestValidator validator, PlacelistDTO placelistDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * Update the placelist
	 *
	 * @param validator
	 * @param placelistDTO
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@PUT
	public PlacelistDTO updatePlacelist(@BeanParam RequestValidator validator, PlacelistDTO placelistDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * Delete the placelist
	 *
	 * @param validator
	 * @param placelistId
	 * @return PlacelistDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@DELETE
	@Path("/{placelistId}")
	public PlacelistDTO deletePlacelist(@BeanParam RequestValidator validator,
			@PathParam("placelistId") Integer placelistId) throws FunctionalException, TechnicalException;

	/**
	 * Get all comments
	 *
	 * @param validator
	 * @param placelistId
	 * @return List<CommentDTO>
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@GET
	@Path("/comments/{placelistId}")
	public List<CommentDTO> getComments(@BeanParam RequestValidator validator,
			@PathParam("placelistId") Integer placelistId) throws FunctionalException, TechnicalException;

	/**
	 * Add the comment
	 *
	 * @param validator
	 * @param placelistId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/comment/{placelistId}")
	public CommentDTO addComment(@BeanParam RequestValidator validator, @PathParam("placelistId") Integer placelistId,
			CommentDTO commentDTO) throws FunctionalException, TechnicalException;

	/**
	 * Delete the comment
	 *
	 * @param validator
	 * @param placelistId
	 * @param commentDTO
	 * @return CommentDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@DELETE
	@Path("/comment/{placelistId}")
	public CommentDTO deleteComment(@BeanParam RequestValidator validator,
			@PathParam("placelistId") Integer placelistId, CommentDTO commentDTO)
			throws FunctionalException, TechnicalException;

	/**
	 * Add the place
	 *
	 * @param validator
	 * @param placelistId
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@POST
	@Path("/{placelistId}/place/{placeId}")
	public PlaceDTO addPlaceToPlacelist(@BeanParam RequestValidator validator,
			@PathParam("placelistId") Integer placelistId, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;

	/**
	 * Remove the place
	 *
	 * @param validator
	 * @param placelistId
	 * @param placeId
	 * @return PlaceDTO
	 * @throws FunctionalException
	 * @throws TechnicalException
	 */
	@DELETE
	@Path("/{placelistId}/place/{placeId}")
	public PlaceDTO removePlaceFromPlacelist(@BeanParam RequestValidator validator,
			@PathParam("placelistId") Integer placelistId, @PathParam("placeId") Integer placeId)
			throws FunctionalException, TechnicalException;
}
