package org.acme.resource;

import static jakarta.ws.rs.core.Response.Status.CREATED;

import java.util.List;

import org.acme.entity.Item;
import org.jboss.resteasy.reactive.RestResponse;


import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/items")
@ApplicationScoped
public class ItemResource {
    
    @GET
    public Uni<List<Item>> get() {
        return Item.listAll(Sort.by("name"));
    }

    @GET
    @Path("/{id}")
    public Uni<Item> getSingle(Long id) {
        return Item.findById(id);
    }

    @POST
    public Uni<RestResponse<Item>> create(Item item) {
        return Panache.withTransaction(item::persist).replaceWith(RestResponse.status(CREATED, item));
    }

}
