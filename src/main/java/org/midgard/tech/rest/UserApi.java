package org.midgard.tech.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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

@Path("/internal/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserApi {

    private static final Logger LOG = Logger.getLogger(UserApi.class);

    @Inject
    UserService userService;

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
}
