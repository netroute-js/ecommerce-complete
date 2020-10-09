package pl.netroute.cart.external.event;

import lombok.Value;
import pl.netroute.event.PublicEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
public class CartFinalized implements PublicEvent {
    UUID cartId;
    UUID clientId;
    List<Product> products;
    BigDecimal totalPrice;
    Instant finalizedAt;

    @Value
    public static class Product {
        UUID productId;
        BigDecimal price;
        int quantity;
    }

}
