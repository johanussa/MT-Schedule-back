package org.midgard.tech.domains.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.midgard.tech.domains.Meta;
import org.midgard.tech.helper.validators.ValidationGroups;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "UsuarioMsg")
public class UserMsg implements Serializable {

    @Schema(hidden = true)
    @Null(message = "No debe ingresar el campo id", groups = { ValidationGroups.Post.class,
            ValidationGroups.Put.class, ValidationGroups.Post_Get.class })
    private ObjectId id;

    @Valid
    @NotNull(message = "El campo data no puede ser nulo", groups = { ValidationGroups.Post.class,
            ValidationGroups.Put.class, ValidationGroups.Post_Get.class })
    private User data;

    @Null(message = "No debe ingresar el campo meta", groups = { ValidationGroups.Post.class,
            ValidationGroups.Put.class, ValidationGroups.Post_Get.class })
    private Meta meta;
}
