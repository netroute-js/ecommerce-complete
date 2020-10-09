package pl.netroute.cart.command.rest.domain;

import lombok.Value;
import pl.netroute.cart.command.service.domain.ProductItem;

import java.util.UUID;

@Value
public class AddProductRequest {
    UUID cartId;
    UUID productId;
    int quantity;

    public ProductItem toProduct() {
        return new ProductItem(productId, quantity);
    }

}
