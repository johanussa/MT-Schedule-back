package org.midgard.tech.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.user.UserData;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class UserDataRepository implements PanacheMongoRepository<UserData> {

    private final Logger LOG = Logger.getLogger(UserDataRepository.class);

    public Optional<UserData> getOneUserData(String documentNumber, String documentType) {

        LOG.infof("@getOneUserData REPO > Inicia consulta a mongo del registro de usuario con tipo de " +
                "documento: %s y numero de documento: %s", documentType, documentNumber);

        return find("data.numeroDocumento = ?1 and data.tipoDocumento = ?2", documentNumber, documentType)
                .firstResultOptional();
    }

    public List<UserData> getRegisteredUsersMongo() {

        LOG.info("@getRegisteredUsersMongo REPO > Inicia obtencion de los usuarios registrados en mongo. estos se " +
                "retornaran ordenados de manera descendente por fecha de creacion");

        return listAll(Sort.descending("meta.fechaCreacion"));
    }

    public Optional<UserData> findUserDataByIdUser(String documentNumber) {

        LOG.infof("@findUserDataByIdUser REPO > Inicia busqueda del registro del usuario con numero de " +
                "documento: %s en mongo", documentNumber);

        return find("data.numeroDocumento = ?1", documentNumber).firstResultOptional();
    }

    public long deleteUserDataMongo(String documentNumber) {

        LOG.infof("@deleteUserDataMongo REPO > Inicia eliminacion de usuario de id: %s en mongo", documentNumber);

        return delete("data.numeroDocumento = ?1", documentNumber);
    }
}
