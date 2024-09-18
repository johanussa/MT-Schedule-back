package org.midgard.tech.services;

import io.vertx.core.http.HttpServerRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.Meta;
import org.midgard.tech.domains.user.User;
import org.midgard.tech.domains.user.UserDTO;
import org.midgard.tech.domains.user.UserData;
import org.midgard.tech.helper.exceptions.MTException;
import org.midgard.tech.repositories.UserDataRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UserService {

    private final Logger LOG = Logger.getLogger(UserService.class);

    @Inject
    UserDataRepository userDataRepository;

    @Inject
    Provider<HttpServerRequest> httpServerRequestProvider;

    public List<UserData> getListRegisteredUser() {

        LOG.debug("@getListRegisteredUser SERV > Inicia ejecucion del servicio para obtener el listado de los " +
                "usuarios registrados en mongo");

        List<UserData> users = getUserData();

        LOG.infof("@getListRegisteredUser SERV > Finaliza obtencion de registros de usuarios y se retorna " +
                "un total de %s registros de mongo. Finaliza ejecucion del servicio para obtener el listado de los" +
                "usuarios registrados", users.size());

        return users;
    }

    public UserData getRegisteredUserMongo(UserData userData) throws MTException {

        LOG.infof("@getRegisteredUserMongo SERV > Inicia ejecucion de servicio para obtener registro del " +
                "usuario con id: %s en mongo", userData.getData().getDocumentNumber());

        String documentType = userData.getData().getDocumentType();
        String documentNumber = userData.getData().getDocumentNumber();

        UserData userMongo = userDataRepository.getOneUserData(documentNumber, documentType).orElseThrow(() -> {

            LOG.errorf("@getRegisteredUserMongo SERV > No se encontro informacion del registro de usuario con " +
                    "tipo de documento: %s y numero de documento: %s en base de datos", documentType, documentNumber);

            return new MTException(Response.Status.NOT_FOUND, "No se encontró el recurso suministrado");
        });

        if (!BCrypt.checkpw(userData.getData().getPassword(), userMongo.getData().getPassword())) {

            LOG.errorf("@getRegisteredUserMongo SERV > El password ingresado no es valido para el cliente " +
                    "con tipo de documento: %s y numero de documento: %s", documentType, documentNumber);

            throw new MTException(Response.Status.BAD_REQUEST, "Error en los recursos suministrados");
        }
        userMongo.setData(getUserDto(userMongo));

        LOG.infof("@getRegisteredUserMongo SERV > Finaliza ejecucion de servicio para obtener registro del " +
                "usuario con id: %s en mongo. Usuario obtenido: %s", documentNumber, userMongo);

        return userMongo;
    }

    public void saveUserDataInMongo(UserData userData) throws UnknownHostException, MTException {

        LOG.infof("@saveUserDataInMongo SERV > Inicia servicio de guardado de usuario en mongo con la data: " +
                "%s. Inicia verificacion de si el usuario ya esta registrado en mongo", userData.getData());

        validateIfUserIsRegistered(userData.getData().getDocumentNumber());

        LOG.infof("@saveUserDataInMongo SERV > Finaliza validacion del usuario si ya ha sido registrado " +
                "previamente. Inicia servicio de verificacion de password del usuario con data: %s si ya ha " +
                "sido encriptada", userData.getData());

        validPasswordEncrypted(userData.getData());

        LOG.infof("@saveUserDataInMongo SERV > Finaliza verificacion de password del usuario con data: " +
                "%s. Inicia edicion al nombre de usuario, el estado y la metadata", userData.getData());

        editUserDataToCreateUser(userData);

        LOG.infof("@saveUserDataInMongo SERV > Finaliza estructura del objeto meta correctamente. Inicia " +
                "almacenamiento del registro en mongo con la data: %s", userData);

        userDataRepository.persist(userData);

        LOG.infof("@saveUserDataInMongo SERV > Finaliza servicio de creacion de usuario con data: %s", userData);
    }

    public void editUserDataInMongo(UserData userData) throws MTException {

        LOG.infof("@editUserDataInMongo SERV > Inicia ejecucion de servicio de edicion de registro de usuario " +
                "con id: %s con la data: %s", userData.getData().getDocumentNumber(), userData.getData());

        String documentNumber = userData.getData().getDocumentNumber();

        UserData userDataMongo = userDataRepository.findUserDataByIdUser(documentNumber).orElseThrow(() -> {

            LOG.errorf("@editUserDataInMongo SERV > El usuario con identificador: %s NO se encuentra " +
                    "registrado en mongo. La solicitud es invalida no se puede editar registro", documentNumber);

            return new MTException(Response.Status.NOT_FOUND, "No se encontró el recurso suministrado");
        });

        LOG.infof("@editUserDataInMongo SERV > El usuario con id: %s si esta registrado, se procede a " +
                "realizar actualizacion de los  en mongo. Inicia verificacion de password", documentNumber);

        validPasswordEncrypted(userData.getData());

        LOG.infof("@editUserDataInMongo SERV > Finaliza verificacion de password del usuario con data: %s. " +
                "Inicia edicion de la informacion del usuario con id: %s", userData.getData(), documentNumber);

        updateUserDataInformation(userDataMongo, userData.getData(), documentNumber);

        LOG.infof("@editUserDataInMongo SERV > Finaliza edicion de usuario con id: %s. Inicia actualizacion " +
                "en mongo con la data: %s", documentNumber, userDataMongo);

        userDataRepository.update(userDataMongo);

        LOG.infof("@editUserDataInMongo SERV > Finaliza actualizacion de registro de usuario con id: %s en " +
                "mongo. Se actualizo el registro con la data: %s", documentNumber, userDataMongo);

        LOG.infof("@editUserData SERV > Finaliza ejecucion de servicio de edicion de registro de usuario " +
                "con id: %s con la data: %s", userData.getData().getDocumentNumber(), userData.getData());
    }

    public void deleteUserDataInMongo(String documentNumber) throws MTException {

        LOG.infof("@deleteUserDataInMongo SERV > Inicia ejecucion del servicio para eliminar registro del " +
                "usuario con id: %s de mongo", documentNumber);

        long update = userDataRepository.deleteUserDataMongo(documentNumber);

        if (update == 0) {

            LOG.errorf("@deleteUserDataInMongo SERV > El registro de usuario con numero de documento: %s No " +
                    "existe en mongo. No se realiza eliminacion. %s registros eliminados", documentNumber, update);

            throw new MTException(Response.Status.NOT_FOUND, "No se encontró el recurso suministrado");
        }
        LOG.infof("@deleteUserDataInMongo SERV > El registro del usuario con numero de documento: %s se " +
                "elimino correctamente de mongo. Finaliza ejecucion del servicio para eliminar usuario y se elimino " +
                "%s registro de la base de datos", documentNumber, update);
    }

    private List<UserData> getUserData() {

        LOG.info("@getUserData SERV > Inicia servicio que consulta y transforma la informacion de cada usuario en " +
                "un DTO para asi retornar solo los datos necesarios");

        return userDataRepository.getRegisteredUsersMongo().stream()
                .peek(userData -> userData.setData(getUserDto(userData))).toList();
    }

    private UserDTO getUserDto(UserData userData) {

        UserDTO userDTO = new UserDTO();

        userDTO.setName(userData.getData().getName());
        userDTO.setLastName(userData.getData().getLastName());
        userDTO.setRole(userData.getData().getRole());
        userDTO.setEmail(userData.getData().getEmail());
        userDTO.setActive(userData.getData().getActive());
        userDTO.setDocumentType(userData.getData().getDocumentType());
        userDTO.setDocumentNumber(userData.getData().getDocumentNumber());

        return userDTO;
    }

    private void updateUserDataInformation(UserData userDataMongo, User editedUser, String idUser) {

        LOG.infof("@updateUserDataInformation SERV > Inicia actualizacion de datos de usuario con id: %s", idUser);

        User userMongo = userDataMongo.getData();

        userMongo.setActive(editedUser.getActive());
        userMongo.setPassword(editedUser.getPassword());
        userMongo.setDocumentType(editedUser.getDocumentType());
        userMongo.setName(capitalizeWords(editedUser.getName()));
        userMongo.setLastName(capitalizeWords(editedUser.getLastName()));

        userDataMongo.getMeta().setLastUpdate(LocalDateTime.now());

        LOG.infof("@updateUserDataInformation SERV > Finaliza actualizacion de datos de usuario con id: %s", idUser);
    }

    private void editUserDataToCreateUser(UserData userData) throws UnknownHostException {

        LOG.infof("@editUserDataToCreateUser SERV > Inicia edicion de datos del usuario para ser almacenados " +
                "en mongo. Los datos que se almacenaran son: %s", userData.getData());

        User user = userData.getData();

        user.setActive(false);
        user.setName(capitalizeWords(user.getName()));
        user.setLastName(capitalizeWords(user.getLastName()));

        LOG.infof("@editUserDataToCreateUser SERV > Finaliza formato al nombre del usuario con data: %s. " +
                "Inicia estructura del objeto meta con la informacion de auditoria", userData.getData());

        userData.setMeta(getMetaToCreateUser());

        LOG.infof("@editUserDataToCreateUser SERV > Finaliza estructura del objeto meta correctamente. " +
                "Finaliza edicion de datos del usuario", userData);
    }

    private void validateIfUserIsRegistered(String idUser) throws MTException {

        LOG.infof("@validateIfUserIsRegistered SERV > Inicia busqueda de registro de usuario con id: %s", idUser);

        if (userDataRepository.findUserDataByIdUser(idUser).isPresent()) {

            LOG.errorf("@validateIfUserIsRegistered SERV > El usuario con identificador: %s ya se encuentra " +
                    "registrado en mongo. La solicitud es invalida, no se puede crear usuario", idUser);

            throw new MTException(Response.Status.BAD_REQUEST, "Error en los recursos suministrados");
        }
        LOG.infof("@validateIfUserIsRegistered SERV > Finaliza busqueda del registro del usuario. El usuario " +
                "con id: %s No ha sido registro. Se continua proceso de registro en mongo", idUser);
    }

    private Meta getMetaToCreateUser() throws UnknownHostException {

        LOG.info("@getMetaToCreateUser SERV > Inicia estructura de objeto meta con informacion de creacion de usuario");

        // TODO - Falta tomar información de quien creó el usuario desde el token

        return Meta.builder()
                .creationDate(LocalDateTime.now())
                .source(httpServerRequestProvider.get().absoluteURI())
                .ipAddress(InetAddress.getLocalHost().getHostAddress())
                .build();
    }

    private String capitalizeWords(String input) {

        LOG.infof("@capitalizeWords SERV > Inicia formato capitalize al nombre: %s", input);

        return Arrays.stream(input.toLowerCase().split(" "))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private void validPasswordEncrypted(User user) {

        LOG.info("@validPasswordEncrypted SERV > Inicia verificacion de la contraseña si ya esta encriptada");

        if (user.getPassword().length() < 25) {

            LOG.infof("@validPasswordEncrypted SERV > Inicia encriptacion de la contraseña del cliente " +
                    "con id: %s", user.getDocumentNumber());

            user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

            LOG.info("@validPasswordEncrypted SERV > Finaliza encriptacion de la contraseña del usuario");
        }
        LOG.info("@validPasswordEncrypted SERV > Finaliza verificacion de la contraseña si ya esta encriptada");
    }

}
