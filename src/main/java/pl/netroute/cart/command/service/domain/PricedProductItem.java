package pl.netroute.cart.command.service.domain;

import lombok.NonNull;
import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class PricedProductItem {
    ProductItem product;
    BigDecimal unitPrice;

    public boolean matchesPricedItem(@NonNull PricedProductItem pricedProductItem) {
        var toMatchPrice = pricedProductItem.getUnitPrice();
        var toMatchProductId = pricedProductItem.getProductId();

        return unitPrice.equals(toMatchPrice) && getProductId().equals(toMatchProductId);
    }

    public boolean canDecreaseQuantityBy(int decreaseQuantityBy) {
        return product.canDecreaseQuantityBy(decreaseQuantityBy);
    }

    public UUID getProductId() {
        return product.getId();
    }

    public int getQuantity() {
        return product.getQuantity();
    }

    public BigDecimal getTotalPrice() {
        return unitPrice.multiply(new BigDecimal(product.getQuantity()));
    }

    public PricedProductItem increaseQuantity(int increaseQuantityBy) {
        return new PricedProductItem(
                product.increaseQuantity(increaseQuantityBy),
                unitPrice
        );
    }

    public PricedProductItem decreaseQuantity(int decreaseQuantityBy) {
        return new PricedProductItem(
                product.decreaseQuantity(decreaseQuantityBy),
                unitPrice
        );
    }
}
