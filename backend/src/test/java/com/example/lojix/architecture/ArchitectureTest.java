package com.example.lojix.architecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import com.tngtech.archunit.library.GeneralCodingRules;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;
import org.springframework.beans.factory.annotation.Autowired;

@AnalyzeClasses(packages = "com.example.lojix", importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    @ArchTest
    static final ArchRule controllers_should_not_depend_on_repositories =
            noClasses()
                    .that().resideInAPackage("..controller..")
                    .should().dependOnClassesThat().resideInAPackage("..infrastructure.repository..");

    @ArchTest
    static final ArchRule domain_should_not_depend_on_outside =
            noClasses()
                    .that().resideInAPackage("..domain..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..")
                    .orShould().dependOnClassesThat().resideInAPackage("..service..")
                    .orShould().dependOnClassesThat().resideInAPackage("..infrastructure..");

    @ArchTest
    static final ArchRule services_should_not_depend_on_controllers =
            noClasses()
                    .that().resideInAPackage("..service..")
                    .should().dependOnClassesThat().resideInAPackage("..controller..");

    @ArchTest
    static final ArchRule controllers_must_be_suffixed_and_annotated =
            classes().that().resideInAPackage("..controller..")
                    .should().haveSimpleNameEndingWith("Controller")
                    .andShould().beAnnotatedWith(RestController.class);

    @ArchTest
    static final ArchRule services_must_be_suffixed_and_annotated =
            classes().that().resideInAPackage("..service..")
                    .should().haveSimpleNameEndingWith("Service")
                    .andShould().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule no_system_out_println =
            GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

    @ArchTest
    static final ArchRule no_generic_exceptions =
            GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;

    @ArchTest
    static final ArchRule no_field_injection =
            noFields()
                    .that().areDeclaredInClassesThat().haveSimpleNameNotEndingWith("Impl")
                    .should().beAnnotatedWith(Autowired.class)
                    .as("Nenhuma classe deve usar @Autowired em propriedades (Field Injection), exceto os Mappers gerados automaticamente. Use injecao por construtor.");

}
