package org.midgard.tech.domains;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta implements Serializable {

    @BsonProperty("fechaCreacion")
    private LocalDateTime creationDate;

    @BsonProperty("fechaUltimaActualizacion")
    private LocalDateTime lastUpdate;

    @Schema(example = "192.168.1.1")
    private String ipAddress;

    @Schema(example = "http://localhost:8089/internal/API_PATH")
    private String source;

    @Schema(example = "1023456789")
    private String idUser;

    @Schema(example = "John Alexander Suarez Mendez")
    private String nameUser;

    @Schema(example = "usuario@soy.sena.edu.co")
    private String emailUser;

}
