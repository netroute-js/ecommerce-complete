package pl.netroute.order.command.service.domain;

import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.test.saga.SagaTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.cart.external.event.CartFinalized;
import pl.netroute.command.CommandSender;
import pl.netroute.order.command.service.domain.command.InitializeOrder;
import pl.netroute.order.command.service.domain.factory.OrderIdFactory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderProcessTest {
    private SagaTestFixture<OrderProcess> orderProcess;
    private OrderIdFactory orderIdFactory;

    @BeforeEach
    public void setup() {
        orderProcess = new SagaTestFixture<>(OrderProcess.class);

        var commandGateway = DefaultCommandGateway
                .builder()
                .commandBus(orderProcess.getCommandBus())
                .build();

        orderIdFactory = mock(OrderIdFactory.class);

        orderProcess.registerResource(new CommandSender(commandGateway));
        orderProcess.registerResource(orderIdFactory);
    }

    @Test
    public void shouldStartSaga() {
        // given
        var cartId = UUID.randomUUID();
        var clientId = UUID.randomUUID();
        var finalizedAt = Instant.now();
        var totalPrice = BigDecimal.TEN;

        var productId = UUID.randomUUID();
        var cartProducts = List.of(
                new CartFinalized.Product(productId, totalPrice, 1)
        );

        var orderProducts = List.of(
                new ProductItem(productId, 1)
        );

        var cartFinalized = new CartFinalized(
                cartId,
                clientId,
                cartProducts,
                totalPrice,
                finalizedAt
        );

        var orderId = UUID.randomUUID();
        when(orderIdFactory.newId()).thenReturn(orderId);

        var initializeOrder = new InitializeOrder(
                orderId,
                clientId,
                orderProducts,
                totalPrice
        );

        // when
        // then
        orderProcess
                .givenNoPriorActivity()
                .whenAggregate(cartId.toString())
                .publishes(cartFinalized)
                .expectActiveSagas(1)
                .expectAssociationWith("cartId", cartId.toString())
                .expectAssociationWith("orderId", orderId.toString())
                .expectDispatchedCommands(initializeOrder);
    }

}
