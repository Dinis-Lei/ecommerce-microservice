package org.acme;

import static jakarta.ws.rs.core.Response.Status.OK;

import java.util.List;

import org.acme.entities.Item;
import org.acme.restclient.CartRestClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestResponse;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@ApplicationScoped
@Path("/analytics")
public class AnalyticsResource {
    
    @Inject
    AnalyticsService analyticsService;

    @Inject
    @RestClient
    CartRestClient cartService;

    @GET
    @Path("/fullstats")
    public JsonObject cartStats() {
        
        int cartCount = analyticsService.getCartCount();
        int emptyCartCount = analyticsService.getEmptyCartCount();
        int fullCartCount = analyticsService.getFullCartCount();
        int maxItemsInCart = analyticsService.getMaxItemsInCart();
        int minItemsInCart = analyticsService.getMinItemsInCart();
        double averageItemsInCart = analyticsService.getAverageItemsInCart();

        JsonObject msg = new JsonObject();
        msg.put("cartCount", cartCount);
        msg.put("emptyCartCount", emptyCartCount);
        msg.put("fullCartCount", fullCartCount);
        msg.put("maxItemsInCart", maxItemsInCart);
        msg.put("minItemsInCart", minItemsInCart);
        msg.put("averageItemsInCart", averageItemsInCart);
        
        return msg;
    }

    @GET
    @Path("/totalnumberofcarts")
    public RestResponse<Integer> totalNumberOfCarts() {
        return RestResponse.status(OK, analyticsService.getCartCount());
    }

    @GET
    @Path("/nemptycarts")
    public RestResponse<Integer> nEmptyCarts() {
        return RestResponse.status(OK, analyticsService.getEmptyCartCount());
    }

    @GET
    @Path("/nfullcarts")
    public RestResponse<Integer> nFullCarts() {
        return RestResponse.status(OK, analyticsService.getFullCartCount());
    }

    @GET
    @Path("/maxitemsincart")
    public RestResponse<Integer> maxItemsInCart() {
        return RestResponse.status(OK, analyticsService.getMaxItemsInCart());
    }

    @GET
    @Path("/minitemsincart")
    public RestResponse<Integer> minItemsInCart() {
        return RestResponse.status(OK, analyticsService.getMinItemsInCart());
    }

    @GET
    @Path("/averageitemsincart")
    public RestResponse<Double> averageItemsInCart() {
        return RestResponse.status(OK, analyticsService.getAverageItemsInCart());
    }

    @GET
    @Path("/topnitemsincarts/{n}")
    public RestResponse<List<Item>> topNItemsInCarts(int n) {
        return RestResponse.status(OK, analyticsService.getTopNItemsInCarts(n));
    }
}
