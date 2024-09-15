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
import org.midgard.tech.domains.Meta;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "SENA_USERS")
public class UserData implements Serializable {

    @JsonIgnore
    @Null(message = "No debe ingresar el campo id")
    private ObjectId id;

    @Valid
    @NotNull(message = "El campo data no puede ser nulo")
    private User data;

    @Null(message = "No debe ingresar el campo meta")
    private Meta meta;
}
