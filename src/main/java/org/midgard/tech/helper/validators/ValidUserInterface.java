package org.midgard.tech.helper.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserValidator.class)
public @interface ValidUserInterface {

    String message() default "Datos de usuario inválidos";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
