package pl.netroute.cart.query.rest.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
public class CartResource {
    UUID cartId;
    UUID clientId;
    String status;
    List<ProductResource> products;
    Instant confirmedAt;

    @Value
    public static class ProductResource {
        UUID productId;
        BigDecimal price;
        int quantity;
    }

}
