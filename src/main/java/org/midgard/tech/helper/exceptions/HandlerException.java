package org.midgard.tech.helper.exceptions;

import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.Builder;
import lombok.Data;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jboss.logging.Logger;

import java.util.UUID;

@Provider
public class HandlerException implements ExceptionMapper<Exception> {

    private final Logger LOG = Logger.getLogger(HandlerException.class);

    @Inject
    jakarta.inject.Provider<HttpServerRequest> httpServerRequestProvider;

    @Override
    public Response toResponse(Exception exception) {
        return mapExceptionToResponseMT(exception);
    }

    private Response mapExceptionToResponseMT(Exception exception) {

        LOG.info("@mapExceptionToResponseMT EXCEPTION > Inicia manejo de exception presentada en el micro servicio " +
                "de Midgard-Tech");

        if (exception instanceof MTException mtException) {

            ProblemException problemException = ProblemException.builder()
                    .host(httpServerRequestProvider.get().getHeader("Host"))
                    .title(mtException.getStatus().getReasonPhrase())
                    .detail(mtException.getMessage())
                    .uri(httpServerRequestProvider.get().absoluteURI())
                    .build();

            return Response
                    .status(mtException.getStatus())
                    .entity(problemException)
                    .build();
        } else {

            UUID idError = UUID.randomUUID();

            LOG.errorf(exception, "@mapExceptionToResponseMT - Failed to process, idError: %s - request " +
                    "to: %s", idError, httpServerRequestProvider.get().absoluteURI());

            return Response
                    .status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(ResponseError.builder()
                            .code(String.valueOf(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()))
                            .idError(idError.toString())
                            .message("Internal Server Error")
                            .build())
                    .build();
        }
    }

    @Data
    @Builder
    public static class ResponseError {

        @Schema(example = "500")
        private String code;

        @Schema(example = "ef156aec-3d78-4f78-9b69-1dd8154f993d")
        private String idError;

        @Schema(example = "Internal Server Error")
        private String message;
    }
}
