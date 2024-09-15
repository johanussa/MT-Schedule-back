package org.midgard.tech.helper.exceptions;

import jakarta.ws.rs.core.Response;
import lombok.Getter;

@Getter
public class MTException extends Exception {

    private final Response.Status status;

    public MTException(Response.Status status, String message) {
        super(message);
        this.status = status;
    }
}
