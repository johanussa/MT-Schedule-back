package org.midgard.tech;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.HashMap;
import java.util.Map;

@Path("/hello")
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    @GET
    public Response hello() {

        Map<String, String> message = new HashMap<>();
        message.put("message", "Hello from Quarkus REST");

        return Response.ok(message).build();
    }
}
