package org.acme.entities;

import java.util.List;

public class Cart {
    
    public Long id;
    public List<CartItem> cartItems;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    

}
