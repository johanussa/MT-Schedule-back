package org.midgard.tech.rest;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;
import org.midgard.tech.domains.classRoom.ClassRoomMsg;
import org.midgard.tech.helper.exceptions.HandlerException;
import org.midgard.tech.helper.exceptions.MTException;
import org.midgard.tech.helper.exceptions.ProblemException;
import org.midgard.tech.services.ClassRoomService;

import java.net.UnknownHostException;

@Path("/internal/class-room")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ClassRoomApi {

    private final Logger LOG = Logger.getLogger(ClassRoomApi.class);

    @Inject
    ClassRoomService classRoomService;

    @GET
    @Tag(name = "Gestión de ambientes")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "200",
                            description = "Se obtuvo la lista de los ambientes registrados exitosamente",
                            content = @Content(schema = @Schema(implementation = ClassRoomMsg.class))
                    ),
                    @APIResponse(
                            responseCode = "404",
                            description = "No se encontró registros de ambientes en la base de datos",
                            content = @Content(schema = @Schema(implementation = ProblemException.class, properties = {
                                    @SchemaProperty(
                                            name = "detail",
                                            example = "No se encontró registro de ambientes"
                                    )
                            }))
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Error interno de servidor",
                            content = @Content(schema = @Schema(implementation = HandlerException.ResponseError.class))
                    )
            }
    )
    public Response getListClassRooms() throws MTException {

        LOG.info("@getListClassRooms SERV > Inicia ejecucion del servicio para obtener lista de ambientes registrados");

        ClassRoomMsg classRoomMsg = classRoomService.getListClassRoomsFromMongo();

        LOG.info("@getListClassRooms SERV > Finaliza ejecucion de servicio para obtener lista de ambientes registrados");

        return Response.ok().entity(classRoomMsg).build();
    }

    @POST
    @Path("/create")
    @Tag(name = "Gestión de ambientes")
    @APIResponses(
            value = {
                    @APIResponse(
                            responseCode = "201",
                            description = "Se registro el listado de ambientes correctamente"
                    ),
                    @APIResponse(
                            responseCode = "400",
                            description = "Error en los recursos suministrados",
                            content = @Content(schema = @Schema(implementation = ProblemException.class))
                    ),
                    @APIResponse(
                            responseCode = "500",
                            description = "Error interno de servidor",
                            content = @Content(schema = @Schema(implementation = HandlerException.ResponseError.class))
                    )
            }
    )
    public Response createListOfClassRooms(
            @RequestBody(
                    name = "classRooms",
                    description = "Objeto con la lista de los ambientes que se registrarán",
                    required = true,
                    content = @Content(example = """
                            {
                                "data": [
                                    "QMA Carpintería mecánica",
                                    "118 SOFTWARE, SISTEMAS, TRANSVERSALES",
                                    "202 COMUNICACIÓN"
                                ]
                            }""")
            )
            @Valid ClassRoomMsg classRooms
    ) throws UnknownHostException {

        LOG.infof("@createListOfClassRooms SERV > Inicia ejecucion del servicio para crear una nueva lista " +
                "de ambientes en la base de datos. Se registran %s ambientes", classRooms.getData().size());

        classRoomService.createListClassRoomsInMongo(classRooms);

        LOG.infof("@createListOfClassRooms SERV > Finaliza ejecucion del servicio para crear una nueva lista " +
                "de ambientes en la base de datos. Se registrarón %s ambientes", classRooms.getData().size());

        return Response.ok().status(Response.Status.CREATED).build();
    }
}
