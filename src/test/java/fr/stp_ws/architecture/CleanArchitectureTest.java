package fr.stp_ws.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * Clean architecture tests
 *
 * @author Jo44
 * @version 1.0 (01/05/2026)
 * @since 01/05/2026
 */
@AnalyzeClasses(packages = "fr.stp_ws", importOptions = ImportOption.DoNotIncludeTests.class)
class CleanArchitectureTest {

	/**
	 * Application layer should not depend on data or presentation layers (except
	 * for data.model package)
	 */
	@ArchTest
	static final ArchRule application_must_not_depend_on_data_or_presentation = noClasses().that()
			.resideInAPackage("..application..").should().dependOnClassesThat()
			.resideInAnyPackage("..data.repository..", "..data.service..", "..data.security..", "..data.config..",
					"..data.mapper..", "..presentation..");

	/**
	 * Domain layer should not depend on other layers
	 */
	@ArchTest
	static final ArchRule domain_must_not_depend_on_other_layers = noClasses().that().resideInAPackage("..domain..")
			.should().dependOnClassesThat()
			.resideInAnyPackage("..application..", "..data..", "..presentation..", "..config..");

	/**
	 * Domain layer should not depend on frameworks
	 */
	@ArchTest
	static final ArchRule domain_must_not_depend_on_frameworks = noClasses().that().resideInAPackage("..domain..")
			.should().dependOnClassesThat()
			.resideInAnyPackage("jakarta..", "org.hibernate..", "io.jsonwebtoken..", "org.glassfish.jersey..");
}
