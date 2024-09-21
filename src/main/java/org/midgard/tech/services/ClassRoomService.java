package org.midgard.tech.services;

import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.Meta;
import org.midgard.tech.domains.classRoom.ClassRoomMsg;
import org.midgard.tech.helper.exceptions.MTException;
import org.midgard.tech.repositories.ClassRoomRepository;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

@ApplicationScoped
public class ClassRoomService {

    private final Logger LOG = Logger.getLogger(ClassRoomService.class);

    @Inject
    ClassRoomRepository classRoomRepository;

    @Inject
    Provider<HttpServerRequest> httpServerRequestProvider;

    public ClassRoomMsg getListClassRoomsFromMongo() throws MTException {

        LOG.info("@getListClassRoomsFromMongo SERV > Inicia ejecucion del servicio para obtener listado de " +
                "ambientes registrados en la base de datos");

        ClassRoomMsg classRoomMsg = classRoomRepository.getListClassRooms().orElseThrow(() -> {

            LOG.errorf("@getListClassRoomsFromMongo SERV > Se presento un error ya que no se encontro ningun " +
                    "registro de una lista de ambientes en la base de datos");

            return new MTException(Response.Status.NOT_FOUND, "Recurso no encontrado");
        });

        LOG.infof("@getListClassRoomsFromMongo SERV > Finaliza ejecucion del servicio para obtener listado " +
                "de ambientes registrados en la base de datos");

        return classRoomMsg;
    }

    public void createListClassRoomsInMongo(ClassRoomMsg classRooms) throws UnknownHostException {

        LOG.infof("@createListClassRoomsInMongo SERV > Inicia ejecucion del servicio para almacenar el " +
                "listado de ambientes en la base de datos. Se guarda la siguiente informacion: %s", classRooms);

        classRooms.setMeta(Meta.builder()
                .creationDate(LocalDateTime.now())
                .source(httpServerRequestProvider.get().absoluteURI())
                .ipAddress(InetAddress.getLocalHost().getHostAddress())
                .build());

        classRoomRepository.persist(classRooms);

        LOG.infof("@createListClassRoomsInMongo SERV > Finaliza ejecucion del servicio para almacenar el " +
                "listado de ambientes en mongo. Se almacenaron %s ambientes", classRooms.getData().size());
    }
}
