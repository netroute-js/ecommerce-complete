package pl.netroute.cart.command.service.domain;

import lombok.NonNull;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import pl.netroute.cart.command.service.ProductPriceService;
import pl.netroute.cart.command.service.domain.command.InitializeCart;
import pl.netroute.cart.command.service.domain.event.CartConfirmed;
import pl.netroute.cart.command.service.domain.event.CartInitialized;
import pl.netroute.cart.command.service.domain.event.ProductAdded;
import pl.netroute.cart.command.service.domain.event.ProductRemoved;
import pl.netroute.cart.command.service.domain.exception.CartIsEmptyException;
import pl.netroute.cart.command.service.domain.exception.NotEnoughProductQuantityException;
import pl.netroute.cart.command.service.domain.exception.ProductNotFoundException;
import pl.netroute.cart.command.service.domain.exception.WrongStateException;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Aggregate(snapshotTriggerDefinition = "cartSnapshotTrigger")
public class Cart {

    @AggregateIdentifier
    private UUID cartId;
    private CartStatus cartStatus;
    private BigDecimal totalPrice;
    private List<PricedProductItem> products;

    private Cart() {
    }

    public Cart(@NonNull InitializeCart initializeCart) {
        CartInitialized cartInitialized = new CartInitialized(
                initializeCart.getCartId(),
                initializeCart.getClientId(),
                CartStatus.PENDING
        );

        AggregateLifecycle.apply(cartInitialized);
    }

    public void addProduct(@NonNull ProductItem product,
                           @NonNull ProductPriceService productPriceService) {
        if(cartStatus != CartStatus.PENDING) {
            throw new WrongStateException("It's illegal to add products to cart in state: " + cartStatus);
        }

        var productId = product.getId();
        var productPrice = productPriceService.getProductPrice(productId);
        var pricedProduct = product.toPricedProduct(productPrice);
        var productAdded = new ProductAdded(cartId, pricedProduct);

        AggregateLifecycle.apply(productAdded);
    }

    public void removeProduct(@NonNull PricedProductItem product) {
        if(cartStatus != CartStatus.PENDING) {
            throw new WrongStateException("It's illegal to remove products from cart in state: " + cartStatus);
        }

        var productId = product.getProductId();
        var foundProduct = findProduct(product)
                .orElseThrow(() -> new ProductNotFoundException("Could not remove product from cart: " + productId));

        var quantityToRemove = product.getQuantity();
        if(!foundProduct.canDecreaseQuantityBy(quantityToRemove)) {
            throw new NotEnoughProductQuantityException("Tried to remove more products than products in the cart");
        }

        var productRemoved = new ProductRemoved(cartId, product);

        AggregateLifecycle.apply(productRemoved);
    }

    public void confirm(@NonNull BigDecimal bonusPrice,
                        @NonNull Clock clock) {
        if(cartStatus != CartStatus.PENDING) {
            throw new WrongStateException("It's illegal to confirm cart in state: " + cartStatus);
        }

        if(products.isEmpty()) {
            throw new CartIsEmptyException("It's illegal to confirm empty cart");
        }

        var totalPrice = calculateTotalPriceWithBonus(bonusPrice);

        var cartConfirmed = new CartConfirmed(
                cartId,
                totalPrice,
                bonusPrice,
                CartStatus.CONFIRMED,
                Instant.now(clock)
        );

        AggregateLifecycle.apply(cartConfirmed);
    }

    private BigDecimal calculateTotalPriceWithBonus(BigDecimal bonusPrice) {
        var calculatedPrice = this.totalPrice.subtract(bonusPrice);

        if(calculatedPrice.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return calculatedPrice;
    }

    private Optional<PricedProductItem> findProduct(PricedProductItem product) {
        return products
                .stream()
                .filter(currentProduct -> currentProduct.matchesPricedItem(product))
                .findFirst();
    }

    private void replaceProductWith(PricedProductItem toReplace,
                                    PricedProductItem replaceWith) {
        var indexOf = products.indexOf(toReplace);

        products.set(indexOf, replaceWith);
    }

    @EventSourcingHandler
    private void handle(CartInitialized cartInitialized) {
        this.cartId = cartInitialized.getCartId();
        this.cartStatus = cartInitialized.getStatus();
        this.totalPrice = BigDecimal.ZERO;
        this.products = new ArrayList<>();
    }

    @EventSourcingHandler
    private void handle(ProductAdded productAdded) {
        var product = productAdded.getProduct();
        var quantity = product.getQuantity();

        findProduct(product)
                .ifPresentOrElse(
                        existingProduct -> replaceProductWith(existingProduct, existingProduct.increaseQuantity(quantity)),
                        () -> products.add(product)
                );

        this.totalPrice = totalPrice.add(product.getTotalPrice());
    }

    @EventSourcingHandler
    private void handle(ProductRemoved productRemoved) {
        var product = productRemoved.getProduct();
        var quantity = product.getQuantity();

        findProduct(product)
                .ifPresentOrElse(
                        existingProduct -> replaceProductWith(existingProduct, existingProduct.decreaseQuantity(quantity)),
                        () -> { throw new ProductNotFoundException("Could not find product: " + product.getProductId()); }
                );

        this.totalPrice = totalPrice.subtract(product.getTotalPrice());
    }

    @EventSourcingHandler
    private void handle(CartConfirmed cartConfirmed) {
        this.cartStatus = cartConfirmed.getStatus();
        this.totalPrice = cartConfirmed.getTotalPrice();
    }

}
