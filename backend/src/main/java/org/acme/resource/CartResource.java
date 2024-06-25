package org.acme.resource;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_MODIFIED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.OK;


import java.util.List;
import java.util.Optional;

import org.acme.entity.Cart;
import org.acme.entity.CartItem;
import org.acme.entity.Item;
import org.jboss.resteasy.reactive.RestResponse;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;

@Path("/cart")
@ApplicationScoped
public class CartResource {

    @GET
    public Uni<List<Cart>> getAllCarts() {
        return Cart.listAll();
    }
    
    @GET
    @Path("/{id}")
    public Uni<Cart> getCartById(Long id) {
        return Cart.findById(id);
    }

    @POST
    public Uni<RestResponse<Cart>> createCart(Cart cart) {
        // Uni<Cart> cartUni = Cart.findById(cart.getId());
        // return cartUni
        //     .onItem().ifNotNull().failWith(new NotFoundException("Cart already exists"))
        //     .onItem().ifNull().continueWith(() -> cart)
        //     .chain(ignore -> Panache.withTransaction(cart::persist))
        //     .replaceWith(RestResponse.status(CREATED, cart));

        return Panache.withTransaction(cart::persist)
            .replaceWith(RestResponse.status(CREATED, cart));
    }

    @POST
    @Path("/{id}/checkout")
    public Uni<RestResponse<String>> checkoutCart(@PathParam("id") Long id) {
        return Panache.withTransaction(() -> Cart.findById(id)
            .onItem().ifNotNull().transformToUni(cart -> {
                Cart cartEntity = (Cart) cart;
                
                if (cartEntity.getCartItems().isEmpty()) {
                    return Uni.createFrom().item(RestResponse.status(OK, "Cart is empty"));
                }


                return hasQuantityExceededStock(cartEntity)
                    .flatMap(hasExceeded -> {
                        if (hasExceeded) {
                            return Uni.createFrom()
                                    .item(RestResponse.status(OK, "Quantity exceeds stock"));
                        }
                        
                        // Proceed with checkout logic
                        // Update item stocks, clear cart items, persist changes, etc.
                        Uni<Void> processItemsUni = Uni.createFrom().voidItem();

                        // Process each cart item sequentially
                        for (CartItem cartItem : cartEntity.getCartItems()) {
                            Item item = cartItem.getItem();
                            item.setStock(item.getStock() - cartItem.getQuantity());
                            item.persist();
                        }

                        // Clear the cart items after all are processed
                        return processItemsUni
                            .chain(ignore -> {
                                cartEntity.getCartItems().clear();
                                return CartItem.delete("cart", cartEntity);
                            })
                            .chain(ignore -> cartEntity.persist())
                            .replaceWith(RestResponse.status(Response.Status.CREATED, "Checkout Successful"));
                });
            })
            .onItem().ifNull().continueWith(RestResponse.status(NOT_FOUND)));
    }

    public static Uni<Boolean> hasQuantityExceededStock(Cart cart) {
        // Create a Uni to accumulate the results
        Uni<Boolean> resultUni = Uni.createFrom().item(false);

        // Loop through each cart item and check asynchronously
        for (CartItem cartItem : cart.getCartItems()) {
            Long itemId = cartItem.getItemId();

            // Fetch the item asynchronously
            Uni<Item> itemUni = Item.findById(itemId);

            // Combine the result with the current accumulated Uni
            resultUni = resultUni
                .flatMap(currentResult -> itemUni
                    .onItem().ifNotNull().transform(item -> {
                        if (cartItem.getQuantity() > item.getStock()) {
                            return true; // Quantity exceeds stock
                        }
                        return currentResult; // No exceedance, keep previous result
                    })
                    .onItem().ifNull().failWith(new NotFoundException("Item not found"))
                );
        }
        return resultUni;
    }

    @PUT
    @Path("/{id}/add")
    public Uni<RestResponse<Cart>> addItem(@PathParam("id") Long id, CartItem newItem) {
        
        return Panache.withTransaction(() -> Cart.findById(id)
        .onItem().ifNotNull().transformToUni(cart -> {
            Cart cartEntity = (Cart) cart;
            // Ensure the item is properly linked to the cart
            newItem.setCart(cartEntity);
            // Find if the item is already in the cart
            Optional<CartItem> existingCartItemOpt = cartEntity.getCartItems().stream()
                .filter(cartItem -> cartItem.equals(newItem))
                .findFirst();
            
            
            return Item.findById(newItem.getItemId())
                .onItem().ifNotNull().transformToUni(item -> {
                    Item itemEntity = (Item) item;
                    if (existingCartItemOpt.isPresent()) {
                        CartItem existingCartItem = existingCartItemOpt.get();
                        
                        // Check if stock is available
                        if (existingCartItem.getQuantity() + newItem.getQuantity() > itemEntity.getStock()) {
                            newItem.setCart(null);
                            return Uni.createFrom().item(RestResponse.status(NOT_MODIFIED, cartEntity));
                        }
                        existingCartItem.setQuantity(existingCartItem.getQuantity() + newItem.getQuantity());
                        return cartEntity.persist().replaceWith(RestResponse.status(CREATED, cartEntity));  
                    }
                    else {
                        // Check if stock is available
                        if (newItem.getQuantity() > itemEntity.getStock()) {
                            newItem.setCart(null);
                            return Uni.createFrom().item(RestResponse.status(NOT_MODIFIED, cartEntity));
                        }
                        cartEntity.addItem(newItem);
                        return cartEntity.persist().replaceWith(RestResponse.status(CREATED, cartEntity));
                    }
                    
                })
                .onItem().ifNull().continueWith(RestResponse.status(NOT_FOUND));

            
        })
        .onItem().ifNull().continueWith(RestResponse.status(NOT_FOUND)));
    }

    // @DELETE
    // @Path("/{id}/remove/{itemId}")
    // public Uni<RestResponse<Cart>> removeItem(@PathParam("id") Long id, @PathParam("itemId") Long itemId) {
    //     return Panache.withTransaction(() -> 
    //         Cart.findById(id)
    //         .onItem().ifNotNull().transformToUni(cart -> {
    //             Cart cartEntity = (Cart) cart;
    //             return CartItem.delete("cart.id = ?1 and item.id = ?2", cartEntity.id, itemId)
    //                             .chain(() -> {
    //                                 cartEntity.getCartItems().removeIf(cartItem -> cartItem.getItemId().equals(itemId));
    //                                 return cartEntity.persist();
    //                             })
    //                            .replaceWith(RestResponse.status(CREATED, cartEntity));
                
    //         })
    //         .onItem().ifNull().continueWith(RestResponse.status(NOT_FOUND)));
    // }

    @DELETE
    @Path("/{id}/clearcart")
    public Uni<RestResponse<Cart>> clearCart(@PathParam("id") Long id) {
        return Panache.withTransaction(() -> Cart.findById(id)
        .onItem().ifNotNull().transformToUni(cart -> {
            Cart cartEntity = (Cart) cart;
            
            // Remove cart items explicitly from the database
            return Panache.withTransaction(() -> {
                return CartItem.delete("cart", cartEntity)
                .chain(() -> {
                    cartEntity.getCartItems().clear();
                    return cartEntity.persist();
                });
            }).replaceWith(RestResponse.status(CREATED, cartEntity));
        })
        .onItem().ifNull().continueWith(RestResponse.status(NOT_FOUND)));
    }

}
