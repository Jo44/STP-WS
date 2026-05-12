package fr.stp_ws;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import fr.stp_ws.application.config.ILimitsProvider;
import fr.stp_ws.application.model.mapper.impl.CommentMapper;
import fr.stp_ws.application.model.mapper.impl.CountMapper;
import fr.stp_ws.application.model.mapper.impl.PhotoMapper;
import fr.stp_ws.application.model.mapper.impl.PlaceMapper;
import fr.stp_ws.application.model.mapper.impl.PlacelistMapper;
import fr.stp_ws.application.model.mapper.impl.UserMapper;
import fr.stp_ws.application.model.mapper.inter.IBasicPlaceMapper;
import fr.stp_ws.application.model.mapper.inter.IBasicPlacelistMapper;
import fr.stp_ws.application.model.mapper.inter.ICommentMapper;
import fr.stp_ws.application.model.mapper.inter.ICountMapper;
import fr.stp_ws.application.model.mapper.inter.IPhotoMapper;
import fr.stp_ws.application.model.mapper.inter.IPlaceMapper;
import fr.stp_ws.application.model.mapper.inter.IPlacelistMapper;
import fr.stp_ws.application.model.mapper.inter.IUserMapper;
import fr.stp_ws.application.repository.IBugReportRepo;
import fr.stp_ws.application.repository.ICommentRepo;
import fr.stp_ws.application.repository.IPhotoRepo;
import fr.stp_ws.application.repository.IPlaceRepo;
import fr.stp_ws.application.repository.IPlacelistRepo;
import fr.stp_ws.application.repository.IUserRepo;
import fr.stp_ws.application.security.ITokenService;
import fr.stp_ws.application.service.impl.PlaceEnrichmentService;
import fr.stp_ws.application.service.impl.PlaceService;
import fr.stp_ws.application.service.impl.PlacelistEnrichmentService;
import fr.stp_ws.application.service.impl.PlacelistService;
import fr.stp_ws.application.service.inter.IPlaceEnrichmentService;
import fr.stp_ws.application.service.inter.IPlaceService;
import fr.stp_ws.application.service.inter.IPlacelistEnrichmentService;
import fr.stp_ws.application.service.inter.IPlacelistService;
import fr.stp_ws.application.usecase.impl.AuthUC;
import fr.stp_ws.application.usecase.impl.BugReportUC;
import fr.stp_ws.application.usecase.impl.PlaceUC;
import fr.stp_ws.application.usecase.impl.PlacelistUC;
import fr.stp_ws.application.usecase.inter.IAuthUC;
import fr.stp_ws.application.usecase.inter.IBugReportUC;
import fr.stp_ws.application.usecase.inter.IPlaceUC;
import fr.stp_ws.application.usecase.inter.IPlacelistUC;
import fr.stp_ws.config.Hibernate;
import fr.stp_ws.config.Settings;
import fr.stp_ws.data.config.LimitsProvider;
import fr.stp_ws.data.repository.BugReportRepo;
import fr.stp_ws.data.repository.CommentRepo;
import fr.stp_ws.data.repository.PhotoRepo;
import fr.stp_ws.data.repository.PlaceRepo;
import fr.stp_ws.data.repository.PlacelistRepo;
import fr.stp_ws.data.repository.UserRepo;
import fr.stp_ws.data.security.TokenService;
import fr.stp_ws.data.service.LoadingService;
import fr.stp_ws.presentation.endpoint.impl.AuthEndpoint;
import fr.stp_ws.presentation.endpoint.impl.BugReportEndpoint;
import fr.stp_ws.presentation.endpoint.impl.PlaceEndpoint;
import fr.stp_ws.presentation.endpoint.impl.PlacelistEndpoint;
import fr.stp_ws.presentation.endpoint.inter.IAuthEndpoint;
import fr.stp_ws.presentation.endpoint.inter.IBugReportEndpoint;
import fr.stp_ws.presentation.endpoint.inter.IPlaceEndpoint;
import fr.stp_ws.presentation.endpoint.inter.IPlacelistEndpoint;
import fr.stp_ws.presentation.filter.CharacterEncodingFilter;
import fr.stp_ws.presentation.model.mapper.JSONMapper;
import jakarta.inject.Singleton;
import jakarta.ws.rs.ApplicationPath;

/**
 * Save the Place - WebService - {STP_WS}
 *
 * @author Jo44
 * @version 1.1 (12/05/2026)
 * @since 01/05/2026
 */
@ApplicationPath("/api")
public class Application extends ResourceConfig {

	private static final Logger LOGGER = LogManager.getLogger(Application.class);

	/** Constructor */
	public Application() {

		// Save the Place - WebService
		LOGGER.info("#############################################");
		LOGGER.info("Save the Place - WebService {STP_WS}");
		LOGGER.info("#############################################");
		LOGGER.info("Configuring dependency injection");

		// Register packages to scan
		LOGGER.info("Registering packages to scan");
		packages("fr.stp_ws.presentation.endpoint.impl");
		packages("fr.stp_ws.presentation.filter");
		packages("fr.stp_ws.presentation.model.mapper");

		// Register configuration classes
		LOGGER.info("Registering configuration classes");
		register(Settings.class);
		register(Hibernate.class);
		register(LoadingService.class);
		register(MultiPartFeature.class);

		// Register support classes
		LOGGER.info("Registering support classes");
		register(CharacterEncodingFilter.class);
		register(JSONMapper.class);

		// Register dependency injection configuration
		register(new AbstractBinder() {

			/** Dependency injection configuration */
			@Override
			protected void configure() {

				LOGGER.info("Registering classes for injection");

				// Configuration
				bind(Settings.class).to(Settings.class).in(Singleton.class);
				bind(Hibernate.class).to(Hibernate.class).in(Singleton.class);
				bind(LimitsProvider.class).to(ILimitsProvider.class).in(Singleton.class);

				// Mappers
				bind(UserMapper.class).to(IUserMapper.class).in(Singleton.class);
				bind(PlaceMapper.class).to(IPlaceMapper.class).in(Singleton.class);
				bind(PlaceMapper.class).to(IBasicPlaceMapper.class).in(Singleton.class);
				bind(PlacelistMapper.class).to(IPlacelistMapper.class).in(Singleton.class);
				bind(PlacelistMapper.class).to(IBasicPlacelistMapper.class).in(Singleton.class);
				bind(CommentMapper.class).to(ICommentMapper.class).in(Singleton.class);
				bind(PhotoMapper.class).to(IPhotoMapper.class).in(Singleton.class);
				bind(CountMapper.class).to(ICountMapper.class).in(Singleton.class);

				// Services
				bind(LoadingService.class).to(LoadingService.class).in(Singleton.class);
				bind(TokenService.class).to(ITokenService.class).in(Singleton.class);
				bind(PlaceService.class).to(IPlaceService.class).in(Singleton.class);
				bind(PlacelistService.class).to(IPlacelistService.class).in(Singleton.class);
				bind(PlaceEnrichmentService.class).to(IPlaceEnrichmentService.class).in(Singleton.class);
				bind(PlacelistEnrichmentService.class).to(IPlacelistEnrichmentService.class).in(Singleton.class);

				// Repositories
				bind(UserRepo.class).to(IUserRepo.class).in(Singleton.class);
				bind(PlaceRepo.class).to(IPlaceRepo.class).in(Singleton.class);
				bind(PlacelistRepo.class).to(IPlacelistRepo.class).in(Singleton.class);
				bind(CommentRepo.class).to(ICommentRepo.class).in(Singleton.class);
				bind(PhotoRepo.class).to(IPhotoRepo.class).in(Singleton.class);
				bind(BugReportRepo.class).to(IBugReportRepo.class).in(Singleton.class);

				// Use-cases
				bind(AuthUC.class).to(IAuthUC.class).in(Singleton.class);
				bind(PlaceUC.class).to(IPlaceUC.class).in(Singleton.class);
				bind(PlacelistUC.class).to(IPlacelistUC.class).in(Singleton.class);
				bind(BugReportUC.class).to(IBugReportUC.class).in(Singleton.class);

				// Endpoints
				bind(AuthEndpoint.class).to(IAuthEndpoint.class).in(Singleton.class);
				bind(PlaceEndpoint.class).to(IPlaceEndpoint.class).in(Singleton.class);
				bind(PlacelistEndpoint.class).to(IPlacelistEndpoint.class).in(Singleton.class);
				bind(BugReportEndpoint.class).to(IBugReportEndpoint.class).in(Singleton.class);
			}
		});
	}
}
