package org.midgard.tech.domains;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonProperty;

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

    private String ipAddress;
    private String source;
    private String idUser;
    private String nameUser;
    private String emailUser;

}
