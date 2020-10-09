package pl.netroute.cart.command.rest.domain;

import lombok.Value;
import pl.netroute.cart.command.service.domain.PricedProductItem;
import pl.netroute.cart.command.service.domain.ProductItem;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class RemoveProductRequest {
    UUID cartId;
    UUID productId;
    int quantity;
    BigDecimal unitPrice;

    public PricedProductItem toPricedProduct() {
        return new PricedProductItem(
                new ProductItem(productId, quantity),
                unitPrice
        );
    }

}
