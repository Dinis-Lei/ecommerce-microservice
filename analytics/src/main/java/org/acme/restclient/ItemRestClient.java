package org.acme.restclient;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import org.acme.entities.Item;
import java.util.List;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/items")
@RegisterRestClient
public interface ItemRestClient {
    
    @GET
    List<Item> getAllItems();

}
