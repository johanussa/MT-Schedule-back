package org.midgard.tech.repositories;

import io.quarkus.mongodb.panache.PanacheMongoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.user.UserData;

import java.util.Optional;

@ApplicationScoped
public class UserDataRepository implements PanacheMongoRepository<UserData> {

    private final Logger LOG = Logger.getLogger(UserDataRepository.class);

    public Optional<UserData> findUserDataByIdUser(String documentNumber) {

        LOG.infof("@findUserDataByIdUser REPO > Inicia busqueda del registro del usuario con numero de " +
                "documento: %s en mongo", documentNumber);

        return find("data.numeroDocumento = ?1", documentNumber).firstResultOptional();
    }
}
