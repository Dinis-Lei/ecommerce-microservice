package org.acme.resource;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

import org.acme.entity.CartItem;

import io.smallrye.mutiny.Uni;

import java.util.List;

@Path("/cart-item")
public class CartItemResource {
    
    @GET
    public Uni<List<CartItem>> get() {
        return CartItem.listAll();
    }

    @GET
    @Path("/hello")
    public String hello() {
        return "hello";
    }

}
