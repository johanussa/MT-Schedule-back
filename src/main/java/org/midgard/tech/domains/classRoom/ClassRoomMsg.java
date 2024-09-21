package org.midgard.tech.domains.classRoom;

import io.quarkus.mongodb.panache.common.MongoEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.midgard.tech.domains.Meta;
import org.midgard.tech.helper.validators.ValidClassRoomInterface;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "AmbienteMsg")
public class ClassRoomMsg implements Serializable {

    @Schema(hidden = true)
    @Null(message = "No debe ingresar el campo id para esta operación")
    private ObjectId id;

    @ValidClassRoomInterface
    @NotNull(message = "EL campo data no puede ser nulo o vacío")
    @Size(min = 2, message = "Debe ingresar al menos dos ambiente en la lista")
    @Schema(example = "[\"103 Programación de software\",\"202 Comunicación\",\"112 Transversales\"]")
    private List<String> data;

    @Null(message = "No debe ingresar el campo meta para esta operación")
    private Meta meta;
}
