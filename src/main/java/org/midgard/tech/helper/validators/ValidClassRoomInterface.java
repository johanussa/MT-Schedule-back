package org.midgard.tech.helper.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ClassRoomValidator.class)
public @interface ValidClassRoomInterface {

    String message() default "Datos de ambientes ingresados inválidos";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};
}
