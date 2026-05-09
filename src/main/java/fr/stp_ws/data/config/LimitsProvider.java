package fr.stp_ws.data.config;

import fr.stp_ws.application.config.ILimitsProvider;
import fr.stp_ws.config.Settings;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

/**
 * Limits provider implementation
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@Singleton
public class LimitsProvider implements ILimitsProvider {

	private final Settings settings;

	/**
	 * Constructor
	 *
	 * @param settings
	 */
	@Inject
	public LimitsProvider(Settings settings) {
		this.settings = settings;
	}

	/* Getters */

	@Override
	public int getUserMaxPlaces() {
		return settings.getInt("limit.user.max.places");
	}

	@Override
	public int getTouristMaxPlaces() {
		return settings.getInt("limit.tourist.max.places");
	}

	@Override
	public int getUserMaxPhotosPerPlace() {
		return settings.getInt("limit.user.max.photos.per.place");
	}

	@Override
	public int getTouristMaxPhotosPerPlace() {
		return settings.getInt("limit.tourist.max.photos.per.place");
	}

	@Override
	public int getUserMaxPlacelists() {
		return settings.getInt("limit.user.max.placelists");
	}

	@Override
	public int getTouristMaxPlacelists() {
		return settings.getInt("limit.tourist.max.placelists");
	}

	@Override
	public int getUserMaxPlacesPerPlacelist() {
		return settings.getInt("limit.user.max.places.per.placelist");
	}

	@Override
	public int getTouristMaxPlacesPerPlacelist() {
		return settings.getInt("limit.tourist.max.places.per.placelist");
	}
}
