package pl.netroute.cart.service.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.cart.command.service.BonusPriceService;
import pl.netroute.cart.command.service.ProductPriceService;
import pl.netroute.cart.command.service.domain.Cart;
import pl.netroute.cart.command.service.domain.CartStatus;
import pl.netroute.cart.command.service.domain.ProductItem;
import pl.netroute.cart.command.service.domain.command.AddProduct;
import pl.netroute.cart.command.service.domain.command.ConfirmCart;
import pl.netroute.cart.command.service.domain.command.InitializeCart;
import pl.netroute.cart.command.service.domain.command.RemoveProduct;
import pl.netroute.cart.command.service.domain.event.CartConfirmed;
import pl.netroute.cart.command.service.domain.event.CartInitialized;
import pl.netroute.cart.command.service.domain.event.ProductAdded;
import pl.netroute.cart.command.service.domain.event.ProductRemoved;
import pl.netroute.cart.command.service.domain.exception.CartIsEmptyException;
import pl.netroute.cart.command.service.domain.exception.NotEnoughProductQuantityException;
import pl.netroute.cart.command.service.domain.exception.ProductNotFoundException;
import pl.netroute.cart.command.service.domain.handler.CartCommandHandler;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartTest {
    private Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    private FixtureConfiguration<Cart> cartFixture;

    private ProductPriceService productPriceService;
    private BonusPriceService bonusPriceService;

    @BeforeEach
    public void setup() {
        productPriceService = mock(ProductPriceService.class);
        bonusPriceService = mock(BonusPriceService.class);

        cartFixture = new AggregateTestFixture<>(Cart.class);

        var cartCommandHandler = new CartCommandHandler(
                fixedClock,
                cartFixture.getRepository(),
                bonusPriceService,
                productPriceService
        );
        cartFixture.registerAnnotatedCommandHandler(cartCommandHandler);
    }

    @Test
    public void shouldInitializeCart() {
        // given
        var cartId = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var initializeCart = new InitializeCart(cartId, clientId);

        // when
        // then
        var expectedEvent = new CartInitialized(cartId, clientId, CartStatus.PENDING);

        cartFixture
                .givenNoPriorActivity()
                .when(initializeCart)
                .expectEvents(expectedEvent);
    }

    @Test
    public void shouldAddProduct() {
        // given
        var cartId = UUID.randomUUID();
        var cartInitialized = asCartInitialized(cartId);

        var productId = UUID.randomUUID();
        var product = new ProductItem(productId, 1);
        var addProduct = new AddProduct(cartId, product);

        var price = BigDecimal.TEN;
        when(productPriceService.getProductPrice(productId)).thenReturn(price);

        // when
        // then
        var expectedEvent = new ProductAdded(cartId, product.toPricedProduct(price));

        cartFixture
                .given(cartInitialized)
                .when(addProduct)
                .expectEvents(expectedEvent);
    }

    private CartInitialized asCartInitialized(UUID cartId) {
        return new CartInitialized(
                cartId,
                UUID.randomUUID(),
                CartStatus.PENDING
        );
    }

    @Test
    public void shouldRemoveProduct() {
        // given
        var cartId = UUID.randomUUID();
        var cartInitialized = asCartInitialized(cartId);

        var productId = UUID.randomUUID();
        var productPrice = BigDecimal.TEN;
        var product = new ProductItem(productId, 1).toPricedProduct(productPrice);
        var productAdded = new ProductAdded(cartId, product);

        var removeProduct = new RemoveProduct(cartId, product);

        // when
        // then
        var expectedEvent = new ProductRemoved(cartId, product);

        cartFixture
                .given(cartInitialized, productAdded)
                .when(removeProduct)
                .expectEvents(expectedEvent);
    }

    @Test
    public void shouldFailRemovingNotExistingProduct() {
        // given
        var cartId = UUID.randomUUID();
        var cartInitialized = asCartInitialized(cartId);

        var productId = UUID.randomUUID();
        var productPrice = BigDecimal.TEN;
        var product = new ProductItem(productId, 1).toPricedProduct(productPrice);

        var removeProduct = new RemoveProduct(cartId, product);

        // when
        // then
        cartFixture
                .given(cartInitialized)
                .when(removeProduct)
                .expectException(ProductNotFoundException.class);
    }

    @Test
    public void shouldFailRemovingMoreQuantitiesThanExisting() {
        // given
        var cartId = UUID.randomUUID();
        var cartInitialized = asCartInitialized(cartId);

        var productId = UUID.randomUUID();
        var productPrice = BigDecimal.TEN;
        var product = new ProductItem(productId, 1).toPricedProduct(productPrice);
        var productAdded = new ProductAdded(cartId, product);

        var removeProduct = new RemoveProduct(cartId, product.increaseQuantity(1));

        // when
        // then
        cartFixture
                .given(cartInitialized, productAdded)
                .when(removeProduct)
                .expectException(NotEnoughProductQuantityException.class);
    }

    @Test
    public void shouldFailConfirmingEmptyCart() {
        // given
        var cartId = UUID.randomUUID();
        var cartInitialized = asCartInitialized(cartId);

        var confirmCart = new ConfirmCart(cartId);

        when(bonusPriceService.calculateBonus(cartId)).thenReturn(BigDecimal.ONE);

        // when
        // then
        cartFixture
                .given(cartInitialized)
                .when(confirmCart)
                .expectException(CartIsEmptyException.class);
    }

    @Test
    public void shouldConfirmCart() {
        // given
        var cartId = UUID.randomUUID();
        var cartInitialized = asCartInitialized(cartId);

        var productId = UUID.randomUUID();
        var productPrice = BigDecimal.TEN;
        var product = new ProductItem(productId, 1).toPricedProduct(productPrice);
        var productAdded = new ProductAdded(cartId, product);

        var confirmCart = new ConfirmCart(cartId);

        var bonusPrice = BigDecimal.ONE;
        when(bonusPriceService.calculateBonus(cartId)).thenReturn(bonusPrice);

        // when
        // then
        var expectedTotalPrice = product.getTotalPrice().subtract(bonusPrice);

        var expectedEvent = new CartConfirmed(
                cartId,
                expectedTotalPrice,
                bonusPrice,
                CartStatus.CONFIRMED,
                fixedClock.instant()
        );

        cartFixture
                .given(cartInitialized, productAdded)
                .when(confirmCart)
                .expectEvents(expectedEvent);
    }

}
