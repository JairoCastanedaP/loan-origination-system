package com.co.bank.credit.loan_origination_system.architecture;

import com.co.bank.credit.loan_origination_system.LoanOriginationSystemApplication;
import com.co.bank.credit.loan_origination_system.shared.domain.exception.DomainException;
import com.co.bank.credit.loan_origination_system.shared.domain.kernel.DomainEvent;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Reglas de la arquitectura hexagonal (análisis de requerimientos, sección 10).
 * Hacen fallar el build (`./mvnw verify`) si alguna capa depende de donde no debe.
 * Las reglas sobre capas aún vacías usan {@code allowEmptyShould(true)} y se activan solas
 * cuando aparecen las primeras clases (fases 04 en adelante).
 */
@AnalyzeClasses(packages = "com.co.bank.credit.loan_origination_system",
        importOptions = ImportOption.DoNotIncludeTests.class)
class HexagonalArchitectureTest {

    private static final String DOMAIN = "..domain..";
    private static final String APPLICATION = "..application..";
    private static final String INFRASTRUCTURE = "..infrastructure..";
    private static final String[] BUSINESS_MODULES = {
            "..loanapplication..", "..customer..", "..creditproduct..", "..survey..", "..statistics.."};

    @ArchTest
    static final ArchRule layersRespectTheDependencyDirection = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .withOptionalLayers(true)
            .layer("Domain").definedBy(DOMAIN)
            .layer("Application").definedBy(APPLICATION)
            .layer("Infrastructure").definedBy(INFRASTRUCTURE)
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure")
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure");

    @ArchTest
    static final ArchRule domainIsPureJava = classes()
            .that().resideInAPackage(DOMAIN)
            .should().onlyDependOnClassesThat().resideInAnyPackage("java..", DOMAIN)
            .because("el dominio es Java puro: sin Spring, JPA, Lombok ni otras librerías");

    @ArchTest
    static final ArchRule applicationDoesNotDependOnFrameworks = noClasses()
            .that().resideInAPackage(APPLICATION)
            .should().dependOnClassesThat().resideInAnyPackage(
                    "org.springframework..", "jakarta..", "org.hibernate..", "lombok..")
            .because("los servicios se crean como @Bean en la configuración de infraestructura")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule springOnlyInInfrastructure = noClasses()
            .that().resideOutsideOfPackage(INFRASTRUCTURE)
            .and().doNotBelongToAnyOf(LoanOriginationSystemApplication.class)
            .should().dependOnClassesThat().resideInAPackage("org.springframework..");

    @ArchTest
    static final ArchRule sharedDoesNotKnowBusinessModules = noClasses()
            .that().resideInAPackage("..shared..")
            .should().dependOnClassesThat().resideInAnyPackage(BUSINESS_MODULES)
            .because("shared es el núcleo común; los módulos dependen de él, no al revés");

    @ArchTest
    static final ArchRule modulesDoNotShareAdapters = slices()
            .matching("com.co.bank.credit.loan_origination_system.(*).infrastructure..")
            .should().notDependOnEachOther()
            .because("un módulo usa a otro a través de sus puertos, nunca de sus adaptadores")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule applicationOfAModuleIsPrivate = slices()
            .matching("com.co.bank.credit.loan_origination_system.(*).application.service..")
            .should().notDependOnEachOther()
            .because("un servicio no llama directamente a los servicios de otro módulo")
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule domainExceptionsExtendDomainException = classes()
            .that().resideInAPackage(DOMAIN).and().haveSimpleNameEndingWith("Exception")
            .should().beAssignableTo(DomainException.class);

    @ArchTest
    static final ArchRule domainEventsAreImmutableRecords = classes()
            .that().resideInAPackage("..domain.event..")
            .should().beRecords().andShould().implement(DomainEvent.class);

    @ArchTest
    static final ArchRule inputPortsAreInterfaces = classes()
            .that().resideInAPackage("..application.port.in..").and().haveSimpleNameEndingWith("UseCase")
            .should().beInterfaces()
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule outputPortsAreInterfaces = classes()
            .that().resideInAPackage("..application.port.out..").and().haveSimpleNameEndingWith("Port")
            .should().beInterfaces()
            .allowEmptyShould(true);

    @ArchTest
    static final ArchRule servicesEndWithService = classes()
            .that().resideInAPackage("..application.service..").and().areNotInterfaces()
            .should().haveSimpleNameEndingWith("Service")
            .allowEmptyShould(true);
}
