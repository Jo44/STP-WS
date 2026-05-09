package fr.stp_ws.application.config;

/**
 * Limits provider interface
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
public interface ILimitsProvider {

	/**
	 * Get maximum places for user category
	 *
	 * @return int
	 */
	public int getUserMaxPlaces();

	/**
	 * Get maximum places for tourist category
	 *
	 * @return int
	 */
	public int getTouristMaxPlaces();

	/**
	 * Get maximum photos per place for user category
	 *
	 * @return int
	 */
	public int getUserMaxPhotosPerPlace();

	/**
	 * Get maximum photos per place for tourist category
	 *
	 * @return int
	 */
	public int getTouristMaxPhotosPerPlace();

	/**
	 * Get maximum placelists for user category
	 *
	 * @return int
	 */
	public int getUserMaxPlacelists();

	/**
	 * Get maximum placelists for tourist category
	 *
	 * @return int
	 */
	public int getTouristMaxPlacelists();

	/**
	 * Get maximum places per placelist for user category
	 *
	 * @return int
	 */
	public int getUserMaxPlacesPerPlacelist();

	/**
	 * Get maximum places per placelist for tourist category
	 *
	 * @return int
	 */
	public int getTouristMaxPlacesPerPlacelist();
}
