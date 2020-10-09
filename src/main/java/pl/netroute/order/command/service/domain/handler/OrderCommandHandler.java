package pl.netroute.order.command.service.domain.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.Repository;
import pl.netroute.order.command.service.domain.Order;
import pl.netroute.order.command.service.domain.command.CancelOrder;
import pl.netroute.order.command.service.domain.command.CompleteOrder;
import pl.netroute.order.command.service.domain.command.InitializeOrder;

import java.time.Clock;

@RequiredArgsConstructor
public class OrderCommandHandler {
    private final Clock clock;
    private final Repository<Order> orderRepository;

    @CommandHandler
    public void handle(@NonNull InitializeOrder initializeOrder) throws Exception {
        orderRepository.newInstance(() -> new Order(initializeOrder, clock));
    }

    @CommandHandler
    public void handle(@NonNull CompleteOrder completeOrder) {
        orderRepository
                .load(completeOrder.getOrderId().toString())
                .execute(order -> order.complete(clock));
    }

    @CommandHandler
    public void handle(@NonNull CancelOrder cancelOrder) {
        orderRepository
                .load(cancelOrder.getOrderId().toString())
                .execute(order -> order.cancel(cancelOrder, clock));
    }

}
