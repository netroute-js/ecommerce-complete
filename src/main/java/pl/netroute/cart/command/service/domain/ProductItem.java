package pl.netroute.cart.command.service.domain;

import lombok.NonNull;
import lombok.Value;
import pl.netroute.cart.command.service.domain.exception.NotEnoughProductQuantityException;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class ProductItem {
    UUID id;
    int quantity;

    public boolean canDecreaseQuantityBy(int decreaseQuantityBy) {
        return quantity - decreaseQuantityBy >= 0;
    }

    public ProductItem increaseQuantity(int increaseQuantityBy) {
        return new ProductItem(id, quantity + increaseQuantityBy);
    }

    public ProductItem decreaseQuantity(int decreaseQuantityBy) {
        var decreasedQuantity = quantity - decreaseQuantityBy;
        if(decreasedQuantity < 0) {
            throw new NotEnoughProductQuantityException("Negative quantity after decreasing for productId: " + id);
        }

        return new ProductItem(id, decreasedQuantity);
    }

    public PricedProductItem toPricedProduct(@NonNull BigDecimal price) {
        return new PricedProductItem(this, price);
    }

}
