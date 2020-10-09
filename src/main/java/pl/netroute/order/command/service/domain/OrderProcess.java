package pl.netroute.order.command.service.domain;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.SagaLifecycle;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import pl.netroute.cart.external.event.CartFinalized;
import pl.netroute.command.CommandSender;
import pl.netroute.order.command.service.domain.command.CancelOrder;
import pl.netroute.order.command.service.domain.command.CompleteOrder;
import pl.netroute.order.command.service.domain.command.InitializeOrder;
import pl.netroute.order.command.service.domain.event.OrderCancelled;
import pl.netroute.order.command.service.domain.event.OrderCompleted;
import pl.netroute.order.command.service.domain.event.OrderInitialized;
import pl.netroute.order.command.service.domain.factory.OrderIdFactory;
import pl.netroute.payment.command.service.domain.command.DiscardPayment;
import pl.netroute.payment.command.service.domain.command.InitializePayment;
import pl.netroute.payment.external.event.PaymentFailed;
import pl.netroute.payment.external.event.PaymentFinalized;
import pl.netroute.payment.query.repository.PaymentEntryRepository;
import pl.netroute.shipment.external.event.PackageRejected;
import pl.netroute.shipment.external.event.PackageSent;

import java.util.UUID;
import java.util.stream.Collectors;

@Saga
@Slf4j
public class OrderProcess {

    @Autowired
    private transient CommandSender commandSender;

    @Autowired
    private transient OrderIdFactory orderIdFactory;

    @Autowired
    private transient PaymentEntryRepository paymentEntryRepository;

    private boolean orderAlreadyCancelled;
    private boolean paymenAlreadyDiscarded;

    @StartSaga
    @SagaEventHandler(associationProperty = "cartId")
    public void handle(@NonNull CartFinalized cartFinalized) {
        var orderId = orderIdFactory.newId();
        log.info("Starting OrderProcess for order " + orderId);

        SagaLifecycle.associateWith("orderId", orderId.toString());

        var products = cartFinalized
                .getProducts()
                .stream()
                .map(product -> new ProductItem(
                        product.getProductId(),
                        product.getQuantity()
                ))
                .collect(Collectors.toUnmodifiableList());

        // init order command
        var initializeOrder = new InitializeOrder(
                orderId,
                cartFinalized.getClientId(),
                products,
                cartFinalized.getTotalPrice()
        );

        commandSender.sendSyncCommand(initializeOrder);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(@NonNull OrderInitialized orderInitialized) {
        var orderId = orderInitialized.getOrderId();
        var paymentId = UUID.randomUUID();
        log.info("Order was initialized. Going to initialize payment " + paymentId + " for order " + orderId);

        var initializePayment = new InitializePayment(
                paymentId,
                orderId,
                orderInitialized.getTotalPrice()
        );

        commandSender.sendSyncCommand(initializePayment);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(@NonNull PaymentFinalized paymentFinalized) {
        var orderId = paymentFinalized.getOrderId();
        log.info("Payment was finalized. Going to send package for order " + orderId);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(@NonNull PackageSent packageSent) {
        var orderId = packageSent.getOrderId();
        log.info("Package was sent. Going to complete order " + orderId);

        var completeOrder = new CompleteOrder(
                orderId
        );

        commandSender.sendSyncCommand(completeOrder);
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(@NonNull PackageRejected packageRejected) {
        var orderId = packageRejected.getOrderId();
        log.info("Package was rejected. Going to cancel order " + orderId);

        var cancelOrder = new CancelOrder(
                orderId,
                "Package was rejected"
        );

        commandSender.sendSyncCommand(cancelOrder);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "orderId")
    public void handle(@NonNull OrderCompleted orderCompleted) {
        log.info("OrderProcess for order {} is completed", orderCompleted.getOrderId());
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(@NonNull OrderCancelled orderCancelled) {
        this.orderAlreadyCancelled = true;

        var orderId = orderCancelled.getOrderId();

        if(!paymenAlreadyDiscarded) {
            log.info("Order was cancelled. Going to discard payment for order " + orderId);

            var payment = paymentEntryRepository
                    .findByOrderId(orderId)
                    .orElseThrow(() -> new IllegalStateException("Could not find Payment for order " + orderId));

            var discardPayment = new DiscardPayment(
                    payment.getId(),
                    orderCancelled.getReason()
            );

            commandSender.sendSyncCommand(discardPayment);
        } else {
            log.info("OrderProcess for order {} ended. Errors detected", orderId);

            SagaLifecycle.end();
        }
    }

    @SagaEventHandler(associationProperty = "orderId")
    public void handle(@NonNull PaymentFailed paymentFailed) {
        this.paymenAlreadyDiscarded = true;

        var orderId = paymentFailed.getOrderId();
        log.info("Payment was discarded for order {}", orderId);

        if(!orderAlreadyCancelled) {
            var cancelOrder = new CancelOrder(
                    orderId,
                    "Payment processing failed"
            );

            commandSender.sendSyncCommand(cancelOrder);
        } else {
            log.info("OrderProcess for order {} ended. Errors detected", orderId);

            SagaLifecycle.end();
        }
    }

}
