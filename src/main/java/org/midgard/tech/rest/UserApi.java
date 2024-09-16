package org.midgard.tech.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.groups.ConvertGroup;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.user.UserData;
import org.midgard.tech.helper.exceptions.MTException;
import org.midgard.tech.helper.validators.ValidationGroups;
import org.midgard.tech.services.UserService;

import java.net.UnknownHostException;
import java.util.List;

@Path("/internal/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserApi {

    private static final Logger LOG = Logger.getLogger(UserApi.class);

    @Inject
    UserService userService;

    @POST
    public Response getUserData(
            @RequestBody(
                    name = "userData",
                    description = "Información del usuario que se va a consultar",
                    required = true
            )
            @NotNull(message = "Debe ingresar el objeto con la información del usuario a registrar")
            @Valid @ConvertGroup(to = ValidationGroups.Post_Get.class) UserData userData
    ) throws MTException {

        LOG.infof("@getUserData API > Inicia ejecucion del servicio para obtener el registro del usuario con " +
                "numero de documento: %s en base de datos", userData.getData().getDocumentNumber());

        UserData userMongo = userService.getRegisteredUserMongo(userData);

        LOG.infof("@getUserData API > Finaliza ejecucion del servicio para obtener el registro del usuario con " +
                "numero de documento: %s. El usuario se obtuvo correctamente", userData.getData().getDocumentNumber());

        return Response.ok().entity(userMongo).build();
    }

    @GET
    @Path("/users")
    public Response getListUsers() {

        LOG.infof("@getListUsers API > Inicia ejecucion del servicio para obtener el listado de todos los " +
                "usuarios registrados en mongo");

        List<UserData> users = userService.getListRegisteredUser();

        LOG.infof("@getListUsers API > Finaliza ejecucion del servicio para obtener el listado de todos los " +
                "usuarios registrados en mongo. Se encontraron: %s registros", users.size());

        return Response.ok().entity(users).build();
    }

    @POST
    @Path("/create")
    public Response createUserData(
            @RequestBody(
                    name = "userData",
                    description = "Objeto con la información del usuario que se va a crear",
                    required = true
            )
            @NotNull(message = "Debe ingresar el objeto data con la información del usuario a registrar")
            @Valid @ConvertGroup(to = ValidationGroups.Post.class) UserData userData
    ) throws UnknownHostException, MTException {

        LOG.infof("@createUserData API > Inicia api de creacion de usuario con la data: %s", userData.getData());

        userService.saveUserDataInMongo(userData);

        LOG.infof("@createUserData API > Finaliza api de creacion de usuario con la data: %s", userData);

        return Response.ok()
                .status(Response.Status.CREATED)
                .build();
    }

    @PUT
    @Path("/edit")
    public Response editUserInfo(
            @RequestBody(
                    name = "userData",
                    description = "Información con la que se actualizara el usuario",
                    required = true
            )
            @Valid @ConvertGroup(to = ValidationGroups.Put.class) UserData userData
    ) throws MTException {

        LOG.infof("@editUserInfo API > Inicia ejecucion de servicio de edicion de usuario con identificador" +
                ": %s. Se actualiza con la data: %s", userData.getData().getDocumentNumber(), userData.getData());

        userService.editUserDataInMongo(userData);

        LOG.infof("@editUserInfo API > Finaliza ejecucion de servicio de edicion de usuario con identificador" +
                ": %s. El registro se actualizo con la data: %s", userData.getData().getDocumentNumber(), userData);

        return Response.ok()
                .status(Response.Status.NO_CONTENT)
                .build();
    }

    @DELETE
    @Path("/delete/{documentNumber}")
    public Response deleteUserData(
            @Parameter(
                    name = "documentNumber",
                    description = "Numero de identificación del usuario a eliminar en mongo",
                    example = "12345670",
                    required = true
            )
            @NotBlank(message = "El valor para número de documento no puede ser nulo o vacío")
            @Size(min = 7, max = 20, message = "El campo número de documento debe contener entre 7 y 20 caracteres")
            @PathParam("documentNumber") String documentNumber
    ) throws MTException {

        LOG.infof("@deleteUserData API > Inicia ejecucion del servicio para eliminar el registro del usuario " +
                "con identificador: %s en mongo", documentNumber);

        userService.deleteUserDataInMongo(documentNumber);

        LOG.infof("@deleteUserData API > Finaliza ejecucion del servicio para eliminar el registro del " +
                "usuario con identificador: %s en mongo", documentNumber);

        return Response.ok()
                .status(Response.Status.NO_CONTENT)
                .build();
    }
}
