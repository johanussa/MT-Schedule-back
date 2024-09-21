package org.midgard.tech.helper.validators;

import io.quarkus.logging.Log;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ClassRoomValidator implements ConstraintValidator<ValidClassRoomInterface, List<String>> {

    @Override
    public boolean isValid(List<String> classRooms, ConstraintValidatorContext context) {

        Log.debugf("@isValid SERV > Inicia validacion de los ambientes agregados en la lista para ser registrados " +
                "en mongo. Se validaran %s ambientes", classRooms.size());

        if (classRooms.isEmpty()) return createConstraintViolation(context, "La lista no puede estar vacía");

        boolean allValid = classRooms.stream().anyMatch(element -> isStringValid(element, context));

        Log.infof("@isValid SERV > Finaliza validacion de los ambientes agregados en la lista para ser " +
                "registrados en mongo. Se validaron %s ambientes. Son validos ? %s", classRooms.size(), allValid);

        return allValid;
    }

    private boolean isStringValid(String classRoom, ConstraintValidatorContext context) {

        if (classRoom == null) {
            return createConstraintViolation(context, "La lista de ambientes no puede tener valores nulos");
        }

        if (classRoom.length() < 6 || classRoom.length() > 50) {

            return createConstraintViolation(context, "El ambiente: " + classRoom + " tiene un formato " +
                    "inválido, debe tener entre 6 y un máximo de 50 caracteres");
        }

        if (!classRoom.matches("^[\\-a-zA-ZÁÉÍÓÚáéíóúñÑ0-9]+( [\\-a-zA-ZÁÉÍÓÚáéíóúñÑ0-9]+)*[.,]?$")) {

            return createConstraintViolation(context, "El ambiente: " + classRoom + " tiene un formato " +
                    "inválido, no puede tener caracteres especiales (*_!$%&/..?¡¿')");
        }

        return true;
    }

    private boolean createConstraintViolation(ConstraintValidatorContext context, String message) {

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();

        return false;
    }
}
