package com.example.lojix.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = MaiorDeIdadeValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MaiorDeIdade {

    String message() default "Usuário deve ter 18 anos ou mais.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
