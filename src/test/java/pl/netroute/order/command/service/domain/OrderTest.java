package pl.netroute.order.command.service.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.order.command.service.domain.command.CancelOrder;
import pl.netroute.order.command.service.domain.command.CompleteOrder;
import pl.netroute.order.command.service.domain.command.InitializeOrder;
import pl.netroute.order.command.service.domain.event.OrderCancelled;
import pl.netroute.order.command.service.domain.event.OrderCompleted;
import pl.netroute.order.command.service.domain.event.OrderInitialized;
import pl.netroute.order.command.service.domain.handler.OrderCommandHandler;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

public class OrderTest {
    private Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    private FixtureConfiguration<Order> orderFixture;

    @BeforeEach
    public void setup() {
        orderFixture = new AggregateTestFixture<>(Order.class);

        var orderCommandHandler = new OrderCommandHandler(
                fixedClock,
                orderFixture.getRepository()
        );
        orderFixture.registerAnnotatedCommandHandler(orderCommandHandler);
    }

    @Test
    public void shouldInitializeOrder() {
        // given
        var orderId = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var products = List.of(
                new ProductItem(UUID.randomUUID(), 1)
        );

        var initializeOrder = new InitializeOrder(
                orderId,
                clientId,
                products,
                BigDecimal.TEN
        );

        // when
        // then
        var orderInitialized = new OrderInitialized(
                orderId,
                clientId,
                products,
                OrderStatus.PENDING,
                initializeOrder.getTotalPrice(),
                fixedClock.instant()
        );

        orderFixture
                .givenNoPriorActivity()
                .when(initializeOrder)
                .expectEvents(orderInitialized);
    }

    @Test
    public void shouldCompleteOrder() {
        // given
        var orderId = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var products = List.of(
                new ProductItem(UUID.randomUUID(), 1)
        );

        var orderInitialized = new OrderInitialized(
                orderId,
                clientId,
                products,
                OrderStatus.PENDING,
                BigDecimal.TEN,
                fixedClock.instant()
        );

        var completeOrder = new CompleteOrder(orderInitialized.getOrderId());

        // when
        // then
        var orderCompleted = new OrderCompleted(
                orderId,
                OrderStatus.COMPLETED,
                fixedClock.instant()
        );

        orderFixture
                .given(orderInitialized)
                .when(completeOrder)
                .expectEvents(orderCompleted);
    }

    @Test
    public void shouldCancelOrder() {
        // given
        var orderId = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var products = List.of(
                new ProductItem(UUID.randomUUID(), 1)
        );

        var orderInitialized = new OrderInitialized(
                orderId,
                clientId,
                products,
                OrderStatus.PENDING,
                BigDecimal.TEN,
                fixedClock.instant()
        );

        var cancelOrder = new CancelOrder(
                orderId,
                "Cancelled by customer"
        );

        // when
        // then
        var orderCancelled = new OrderCancelled(
                orderId,
                OrderStatus.CANCELLED,
                cancelOrder.getReason(),
                fixedClock.instant()
        );

        orderFixture
                .given(orderInitialized)
                .when(cancelOrder)
                .expectEvents(orderCancelled);
    }
}
