package org.acme.resource;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;

import java.util.List;

import org.acme.entity.CartItem;
import org.acme.entity.Item;
import org.jboss.resteasy.reactive.RestResponse;


import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
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
        return Panache.withTransaction(item::persistAndFlush).replaceWith(RestResponse.status(CREATED, item));
    }

    @PUT
    @Path("/{id}")
    public Uni<RestResponse<Item>> update(Long id, Item item) {
        return Panache.withTransaction(() -> {
            return Item.findById(id)
                .onItem().ifNotNull().invoke(i -> {
                    Item upItem = (Item) i;
                    upItem.setName(item.getName());
                    upItem.setPrice(item.getPrice());
                    upItem.setStock(item.getStock());
                })
                .onItem().ifNull().continueWith(item);
        }).replaceWith(RestResponse.status(CREATED, item));
    }

    @DELETE
    @Path("/{id}")
    public Uni<RestResponse<Void>> delete(Long id) {
        return Panache.withTransaction(() ->{
            return CartItem.delete("item.id", id).chain(() -> Item.deleteById(id));
        }).replaceWith(RestResponse.status(CREATED));
    }

}
