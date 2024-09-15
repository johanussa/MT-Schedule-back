package org.midgard.tech.helper.validators;

import io.quarkus.logging.Log;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.midgard.tech.domains.user.User;
import org.midgard.tech.domains.user.UserDocumentTypeEnum;
import org.midgard.tech.domains.user.UserRoleEnum;

import java.util.Arrays;

public class UserValidator implements ConstraintValidator<ValidUserInterface, User> {

    @Override
    public boolean isValid(User value, ConstraintValidatorContext context) {

        Log.infof("@isValid > Inicia validacion del usuario con la data: %s", value);

        if (Arrays.stream(UserDocumentTypeEnum.values())
                .noneMatch(documentType -> documentType.getValue().equals(value.getDocumentType()))) {

            String message = "El tipo de documento ingresado: " + value.getDocumentType() + ", no es valido";

            Log.errorf("@isValid > %s", message);

            createConstraintViolation(context, message);

            return false;
        }

        if (value.getRole() != null && Arrays.stream(UserRoleEnum.values())
                .noneMatch(enumRole -> enumRole.getValue().equals(value.getRole()))) {

            String message = "El rol de usuario ingresado: " + value.getRole() + ", no es valido";

            Log.errorf("@isValid > %s", message);

            createConstraintViolation(context, message);

            return false;
        }

        Log.infof("@isValid > Finaliza validacion del usuario con la data: %s. Los datos son correctos", value);

        return true;
    }

    private void createConstraintViolation(ConstraintValidatorContext context, String message) {

        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
