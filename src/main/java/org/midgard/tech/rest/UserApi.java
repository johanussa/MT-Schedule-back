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
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.user.UserMsg;
import org.midgard.tech.helper.exceptions.HandlerException;
import org.midgard.tech.helper.exceptions.MTException;
import org.midgard.tech.helper.exceptions.ProblemException;
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
    @Tag(name = "Gestión de usuarios")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Se retorna la información del usuario correctamente",
                            content = @Content(schema = @Schema(implementation = UserMsg.class))
                    ),
                    @APIResponse(
                            responseCode = "400",
                            description = "Error en recursos suministrados",
                            content = @Content(schema = @Schema(implementation = ProblemException.class))
                    ),
                    @APIResponse(
                            responseCode = "404",
                            description = "Recurso no encontrado",
                            content = @Content(schema = @Schema(implementation = ProblemException.class))
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Error interno de servidor",
                            content = @Content(schema = @Schema(implementation = HandlerException.ResponseError.class))
                    )
            }
    )
    @Operation(
            summary = "Obtención de un usuario registrado",
            description = "Permite obtener la información de un usuario por los datos proporcionados"
    )
    public Response getUserData(
            @RequestBody(
                    name = "userMsg",
                    description = "Información del usuario que se va a consultar",
                    required = true,
                    content = @Content(example = """
                            {
                                "data": {
                                    "documentNumber": "10345678190",
                                    "documentType": "CEDULA_DE_CIUDADANIA",
                                    "password": "password"
                                }
                            }"""
                    )
            )
            @NotNull(message = "Debe ingresar el objeto con la información del usuario a registrar")
            @Valid @ConvertGroup(to = ValidationGroups.Post_Get.class) UserMsg userMsg
    ) throws MTException {

        LOG.infof("@getUserData API > Inicia ejecucion del servicio para obtener el registro del usuario con " +
                "numero de documento: %s en base de datos", userMsg.getData().getDocumentNumber());

        UserMsg userMongo = userService.getRegisteredUserMongo(userMsg);

        LOG.infof("@getUserData API > Finaliza ejecucion del servicio para obtener el registro del usuario con " +
                "numero de documento: %s. El usuario se obtuvo correctamente", userMsg.getData().getDocumentNumber());

        return Response.ok().entity(userMongo).build();
    }

    @GET
    @Path("/users")
    @Tag(name = "Gestión de usuarios")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Se obtuvo la información de todos los usuarios registrados",
                            content = @Content(schema = @Schema(type = SchemaType.ARRAY, implementation = UserMsg.class))
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Error interno de servidor",
                            content = @Content(schema = @Schema(implementation = HandlerException.ResponseError.class))
                    )
            }
    )
    @Operation(
            summary = "Obtención de todos los usuarios registrados",
            description = "Permite obtener un listado con la información de los usuario registrados"
    )
    public Response getListUsers() {

        LOG.info("@getListUsers API > Inicia ejecucion del servicio para obtener el listado de todos los usuarios " +
                "registrados en mongo");

        List<UserMsg> users = userService.getListRegisteredUser();

        LOG.infof("@getListUsers API > Finaliza ejecucion del servicio para obtener el listado de todos los " +
                "usuarios registrados en mongo. Se encontraron: %s registros", users.size());

        return Response.ok().entity(users).build();
    }

    @POST
    @Path("/create")
    @Tag(name = "Gestión de usuarios")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "El usuario fue registrado exitosamente en la base de datos"
                    ),
                    @APIResponse(
                            responseCode = "400",
                            description = "Error en recursos suministrados",
                            content = @Content(schema = @Schema(implementation = ProblemException.class))
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Error interno de servidor",
                            content = @Content(schema = @Schema(implementation = HandlerException.ResponseError.class))
                    )
            }
    )
    @Operation(
            summary = "Creación de un usuario nuevo",
            description = "Permite crear el registro de un usuario nuevo en la base de datos"
    )
    public Response createUserData(
            @RequestBody(
                    name = "userMsg",
                    description = "Objeto con la información del usuario que se va a crear",
                    required = true,
                    content = @Content(example = """
                            {
                                "data": {
                                    "name": "John Alexander",
                                    "lastName": "Gonzalez Rojas",
                                    "documentType": "CEDULA_DE_CIUDADANIA",
                                    "documentNumber": "1023456789",
                                    "email": "jhongonzalex@soy.sena.edu.co",
                                    "password": "password",
                                    "role": "INSTRUCTOR"
                                }
                            }"""
                    )
            )
            @NotNull(message = "Debe ingresar el objeto data con la información del usuario a registrar")
            @Valid @ConvertGroup(to = ValidationGroups.Post.class) UserMsg userMsg
    ) throws UnknownHostException, MTException {

        LOG.infof("@createUserData API > Inicia api de creacion de usuario con la data: %s", userMsg.getData());

        userService.saveUserDataInMongo(userMsg);

        LOG.infof("@createUserData API > Finaliza api de creacion de usuario con la data: %s", userMsg);

        return Response.ok()
                .status(Response.Status.CREATED)
                .build();
    }

    @PUT
    @Path("/edit")
    @Tag(name = "Gestión de usuarios")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "204",
                            description = "El usuario se actualizó correctamente"
                    ),
                    @APIResponse(
                            responseCode = "400",
                            description = "Error en recursos suministrados",
                            content = @Content(schema = @Schema(implementation = ProblemException.class))
                    ),
                    @APIResponse(
                            responseCode = "404",
                            description = "Recurso no encontrado",
                            content = @Content(schema = @Schema(implementation = ProblemException.class))
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Error interno de servidor",
                            content = @Content(schema = @Schema(implementation = HandlerException.ResponseError.class))
                    )
            }
    )
    @Operation(
            summary = "Edición de un usuario registrado",
            description = "Permite modificar la información de un usuario registrado con los datos proporcionados"
    )
    public Response editUserInfo(
            @RequestBody(
                    name = "userMsg",
                    description = "Información con la que se actualizara el usuario",
                    required = true,
                    content = @Content(example = """
                            {
                                "data": {
                                    "name": "Otro nombre",
                                    "lastName": "Otro apellido",
                                    "documentType": "CEDULA_DE_CIUDADANIA",
                                    "documentNumber": "1034567819",
                                    "password": "password",
                                    "active": "false"
                                }
                            }""")
            )
            @Valid @ConvertGroup(to = ValidationGroups.Put.class) UserMsg userMsg
    ) throws MTException {

        LOG.infof("@editUserInfo API > Inicia ejecucion de servicio de edicion de usuario con identificador" +
                ": %s. Se actualiza con la data: %s", userMsg.getData().getDocumentNumber(), userMsg.getData());

        userService.editUserDataInMongo(userMsg);

        LOG.infof("@editUserInfo API > Finaliza ejecucion de servicio de edicion de usuario con identificador" +
                ": %s. El registro se actualizo con la data: %s", userMsg.getData().getDocumentNumber(), userMsg);

        return Response.ok()
                .status(Response.Status.NO_CONTENT)
                .build();
    }

    @DELETE
    @Path("/delete/{documentNumber}")
    @Tag(name = "Gestión de usuarios")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "204",
                            description = "El registro del usuario con el número proporcionado se eliminó correctamente"
                    ),
                    @APIResponse(
                            responseCode = "404",
                            description = "Recurso no encontrado",
                            content = @Content(schema = @Schema(implementation = ProblemException.class))
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Error interno de servidor",
                            content = @Content(schema = @Schema(implementation = HandlerException.ResponseError.class))
                    )
            }
    )
    @Operation(
            summary = "Eliminación de un usuario registrado",
            description = "Permite eliminar el registro de un usuario registrado por su número de identificación"
    )
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
