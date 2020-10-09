package pl.netroute.order.command.service.domain;

import lombok.NonNull;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import pl.netroute.order.command.service.domain.command.CancelOrder;
import pl.netroute.order.command.service.domain.command.InitializeOrder;
import pl.netroute.order.command.service.domain.event.OrderCancelled;
import pl.netroute.order.command.service.domain.event.OrderCompleted;
import pl.netroute.order.command.service.domain.event.OrderInitialized;
import pl.netroute.order.command.service.domain.exception.WrongOrderStatusException;

import java.time.Clock;
import java.util.UUID;

@Aggregate
public class Order {

    @AggregateIdentifier
    private UUID orderId;
    private OrderStatus status;

    private Order() {
    }

    public Order(@NonNull InitializeOrder initializeOrder,
                 @NonNull Clock clock) {
        var orderInitialized = new OrderInitialized(
                initializeOrder.getOrderId(),
                initializeOrder.getClientId(),
                initializeOrder.getProducts(),
                OrderStatus.PENDING,
                initializeOrder.getTotalPrice(),
                clock.instant()
        );

        AggregateLifecycle.apply(orderInitialized);
    }

    public void complete(@NonNull Clock clock) {
        if(status != OrderStatus.PENDING) {
            throw new WrongOrderStatusException("It's illegal to complete order in state: " + status);
        }

        var orderCompleted = new OrderCompleted(
                orderId,
                OrderStatus.COMPLETED,
                clock.instant()
        );

        AggregateLifecycle.apply(orderCompleted);
    }

    public void cancel(@NonNull CancelOrder cancelOrder,
                       @NonNull Clock clock) {
        if(status != OrderStatus.PENDING) {
            throw new WrongOrderStatusException("It's illegal to complete order in state: " + status);
        }

        var orderCancelled = new OrderCancelled(
                orderId,
                OrderStatus.CANCELLED,
                cancelOrder.getReason(),
                clock.instant()
        );

        AggregateLifecycle.apply(orderCancelled);
    }

    @EventSourcingHandler
    private void handle(OrderInitialized orderInitialized) {
        this.orderId = orderInitialized.getOrderId();
        this.status = orderInitialized.getStatus();
    }

    @EventSourcingHandler
    private void handle(OrderCompleted orderCompleted) {
        this.status = orderCompleted.getStatus();
    }

    @EventSourcingHandler
    private void handle(OrderCancelled orderCancelled) {
        this.status = orderCancelled.getStatus();
    }

}
