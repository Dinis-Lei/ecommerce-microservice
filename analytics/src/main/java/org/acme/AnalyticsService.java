package org.acme;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.acme.entities.Cart;
import org.acme.entities.CartItem;
import org.acme.entities.Item;
import org.acme.restclient.CartRestClient;
import org.acme.restclient.ItemRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AnalyticsService {
    
    @Inject
    @RestClient
    CartRestClient cartRestClient;

    @Inject
    @RestClient
    ItemRestClient itemRestClient;

    public Integer getCartCount() {
        return cartRestClient.getAllCarts().size();
    }

    public Integer getEmptyCartCount() {
        int emptyCartCount = 0;
        for (Cart cart : cartRestClient.getAllCarts()) {
            if (cart.getCartItems().isEmpty()) {
                emptyCartCount++;
            }
        }
        return emptyCartCount;
    }

    public Integer getFullCartCount() {
        return getCartCount() - getEmptyCartCount();
    }

    public Integer getMaxItemsInCart() {
        int maxItems = 0;
        for (Cart cart : cartRestClient.getAllCarts()) {
            int cartSize = cart.getCartItems().size();
            if (cartSize > maxItems) {
                maxItems = cartSize;
            }
        }
        return maxItems;
    }

    public Integer getMinItemsInCart() {
        int minItems = Integer.MAX_VALUE;
        for (Cart cart : cartRestClient.getAllCarts()) {
            int cartSize = cart.getCartItems().size();
            if (cartSize < minItems) {
                minItems = cartSize;
            }
        }
        return minItems;
    }

    public Double getAverageItemsInCart() {
        return (double) itemRestClient.getAllItems().size() / getCartCount();
    }

    public List<Item> getTopNItemsInCarts(int n) {
        List<Cart> carts = cartRestClient.getAllCarts();
        
        Map<Item, Integer> itemCounts = new HashMap<>();

        for (Cart cart : carts) {
            for (CartItem cartItem : cart.getCartItems()) {
                itemCounts.put(cartItem.getItem(), itemCounts.getOrDefault(cartItem.getItem(), 0) + cartItem.getQuantity());
            }
        }
        
        return itemCounts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(n)
            .map(Map.Entry::getKey)
            .toList();
    }

}
