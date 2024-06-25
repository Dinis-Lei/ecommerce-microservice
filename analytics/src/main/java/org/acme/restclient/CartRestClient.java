package org.acme.restclient;

import java.util.List;

import org.acme.entities.Cart;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/cart")
@RegisterRestClient
public interface CartRestClient {
    
    @GET
    List<Cart> getAllCarts();
}
