package org.midgard.tech.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.classRoom.ClassRoomMsg;

import java.util.Optional;

@ApplicationScoped
public class ClassRoomRepository implements PanacheMongoRepository<ClassRoomMsg> {

    private final Logger LOG = Logger.getLogger(ClassRoomMsg.class);

    public Optional<ClassRoomMsg> getListClassRooms() {

        LOG.debug("@getListClassRooms REPO > Inicia consulta en mongo de la lista de los ambientes registrados");

        return findAll(Sort.descending("meta.fechaCreacion")).firstResultOptional();
    }
}
