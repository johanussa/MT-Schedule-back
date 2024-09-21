package org.midgard.tech.domains.user;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.midgard.tech.helper.validators.ValidUserInterface;
import org.midgard.tech.helper.validators.ValidationGroups;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidUserInterface
public class User implements Serializable {

    @BsonProperty("nombre")
    @Schema(example = "Johan Sebastian")
    @NotBlank(message = "El campo name no puede ser nulo o vacío")
    @Size(min = 3, max = 40, message = "El campo name debe contener entre 3 y 40 caracteres")
    @Pattern(regexp = "^[a-zA-Z]+( [a-zA-Z]+)*$", message = "El nombre no debe contener valores numéricos ni " +
            "caracteres especiales ni espacios vacíos al inicio o al final")
    @Null(message = "No debe ingresar el nombre para obtener data de usuario", groups = ValidationGroups.Post_Get.class)
    private String name;

    @BsonProperty("apellido")
    @Schema(example = "Gonzalez Rojas")
    @NotBlank(message = "El campo lastName no puede ser nulo o vacío")
    @Size(min = 3, max = 40, message = "El campo lastName debe contener entre 3 y 40 caracteres")
    @Pattern(regexp = "^[a-zA-Z]+( [a-zA-Z]+)*$", message = "El apellido no debe contener valores numéricos ni " +
            "caracteres especiales ni espacios vacíos al inicio o al final")
    @Null(message = "No debe ingresar el apellido para obtener data del usuario", groups = ValidationGroups.Post_Get.class)
    private String lastName;

    @Schema(example = "1023456789")
    @BsonProperty("numeroDocumento")
    @NotBlank(message = "El campo documentNumber no puede ser nulo o vacío", groups = {ValidationGroups.Post_Get.class,
            ValidationGroups.Put.class, ValidationGroups.Post.class})
    @Size(min = 7, max = 20, message = "El campo documentNumber debe contener entre 7 y 20 caracteres")
    private String documentNumber;

    @BsonProperty("tipoDocumento")
    @Schema(example = "CEDULA_DE_CIUDADANIA")
    @NotBlank(message = "El campo documentType no puede ser nulo o vacío", groups = {ValidationGroups.Post_Get.class,
            ValidationGroups.Put.class, ValidationGroups.Post.class})
    private String documentType;

    @BsonProperty("correo")
    @Schema(example = "usuario@misena.edu.co")
    @Null(message = "No debe enviar datos para el correo del usuario", groups = {ValidationGroups.Put.class,
            ValidationGroups.Post_Get.class})
    @NotBlank(message = "El campo email no puede ser nulo o vacío", groups = ValidationGroups.Post.class)
    @Pattern(regexp = "^\\w+@(misena|soy\\.sena)\\.edu\\.co$", message = "El formato del correo es incorrecto, " +
            "debe ser de dominio SENA (misena o soy.sena.edu.co)")
    private String email;

    @Schema(example = "password")
    @NotBlank(message = "El campo password no puede ser nulo o vacío", groups = {ValidationGroups.Post_Get.class,
            ValidationGroups.Put.class, ValidationGroups.Post.class})
    @Size(min = 8, message = "La contraseña debe tener mínimo 8 caracteres")
    private String password;

    @BsonProperty("activo")
    @Schema(type = SchemaType.BOOLEAN)
    @NotNull(message = "El campo active no debe ser nulo", groups = ValidationGroups.Put.class)
    @Null(message = "No debe enviar datos para el campo active", groups = {ValidationGroups.Post.class,
            ValidationGroups.Post_Get.class})
    private Boolean active;

    @BsonProperty("rol")
    @Schema(example = "ADMINISTRADOR")
    @Null(message = "No debe enviar datos para el rol del usuario", groups = {ValidationGroups.Put.class,
            ValidationGroups.Post_Get.class})
    @NotBlank(message = "El campo role no debe ser nulo o vació", groups = ValidationGroups.Post.class)
    private String role;

    @Override
    public String toString() {
        return "User: {" +
                "name:'" + name + '\'' +
                ", lastName:'" + lastName + '\'' +
                ", documentNumber:'" + documentNumber + '\'' +
                ", documentType:'" + documentType + '\'' +
                ", email:'" + email + '\'' +
                ", active:" + active +
                ", role:'" + role + '\'' +
                '}';
    }
}
