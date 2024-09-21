package org.midgard.tech.helper.exceptions;

import io.vertx.core.http.HttpServerRequest;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import java.util.stream.Collectors;

@Provider
public class ValidatorHandlerException implements ExceptionMapper<ConstraintViolationException> {

    @Inject
    jakarta.inject.Provider<HttpServerRequest> httpServerRequestProvider;

    @Override
    public Response toResponse(ConstraintViolationException exception) {

        ProblemException problemException = ProblemException.builder()
                .host(httpServerRequestProvider.get().getHeader("Host"))
                .title("Solicitud inválida")
                .detail(exception.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .collect(Collectors.joining(";")))
                .uri(httpServerRequestProvider.get().absoluteURI())
                .build();

        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON)
                .entity(problemException)
                .build();
    }
}
