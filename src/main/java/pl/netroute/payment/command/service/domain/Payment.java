package pl.netroute.payment.command.service.domain;

import lombok.NonNull;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import pl.netroute.payment.command.service.domain.command.CompletePayment;
import pl.netroute.payment.command.service.domain.command.DiscardPayment;
import pl.netroute.payment.command.service.domain.command.InitializePayment;
import pl.netroute.payment.command.service.domain.event.PaymentCompleted;
import pl.netroute.payment.command.service.domain.event.PaymentDiscarded;
import pl.netroute.payment.command.service.domain.event.PaymentInitialized;
import pl.netroute.payment.command.service.domain.exception.WrongPaymentStatusException;

import java.time.Clock;
import java.util.UUID;

@Aggregate
public class Payment {

    @AggregateIdentifier
    private UUID paymentId;
    private PaymentStatus status;

    private Payment() {
    }

    public Payment(@NonNull InitializePayment initializePayment,
                   @NonNull Clock clock) {
        var paymentInitialized = new PaymentInitialized(
                initializePayment.getPaymentId(),
                initializePayment.getOrderId(),
                PaymentStatus.PENDING,
                initializePayment.getAmount(),
                clock.instant()
        );

        AggregateLifecycle.apply(paymentInitialized);
    }

    public void complete(@NonNull CompletePayment completePayment,
                         @NonNull Clock clock) {
        if(status != PaymentStatus.PENDING) {
            throw new WrongPaymentStatusException("It's illegal to complete payment in state " + status);
        }

        var paymentCompleted = new PaymentCompleted(
                paymentId,
                PaymentStatus.COMPLETED,
                clock.instant()
        );

        AggregateLifecycle.apply(paymentCompleted);
    }

    public void discard(@NonNull DiscardPayment discardPayment,
                        @NonNull Clock clock) {
        if(status == PaymentStatus.DISCARDED) {
            return;
        }

        var paymentDiscarded = new PaymentDiscarded(
                paymentId,
                PaymentStatus.DISCARDED,
                discardPayment.getReason(),
                clock.instant()
        );

        AggregateLifecycle.apply(paymentDiscarded);
    }

    @EventSourcingHandler
    private void handle(PaymentInitialized paymentInitialized) {
        this.paymentId = paymentInitialized.getPaymentId();
        this.status = paymentInitialized.getStatus();
    }

    @EventSourcingHandler
    private void handle(PaymentCompleted paymentCompleted) {
        this.status = paymentCompleted.getStatus();
    }

    @EventSourcingHandler
    private void handle(PaymentDiscarded paymentDiscarded) {
        this.status = paymentDiscarded.getStatus();
    }

}
