package pl.netroute.payment.query.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.annotation.MessageIdentifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.netroute.event.EventSender;
import pl.netroute.event.processor.service.IdempotentEventProcessorService;
import pl.netroute.payment.command.service.domain.event.PaymentCompleted;
import pl.netroute.payment.command.service.domain.event.PaymentDiscarded;
import pl.netroute.payment.command.service.domain.event.PaymentInitialized;
import pl.netroute.payment.external.event.PaymentFailed;
import pl.netroute.payment.external.event.PaymentFinalized;
import pl.netroute.payment.query.repository.PaymentEntryRepository;
import pl.netroute.payment.query.repository.domain.PaymentEntry;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventHandler {
    private static final String CONTEXT = "PaymentView";

    private final EventSender eventSender;
    private final PaymentEntryRepository paymentEntryRepository;
    private final IdempotentEventProcessorService idempotentEventProcessorService;

    @EventHandler
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(@NonNull PaymentInitialized paymentInitialized,
                       @NonNull @MessageIdentifier String eventId) {
        consumeOrSkipIfConsumed(
                eventId,
                () -> {
                    log.info("Handling {}", paymentInitialized);

                    var payment = new PaymentEntry();
                    payment.initializePayment(
                            paymentInitialized.getPaymentId(),
                            paymentInitialized.getOrderId(),
                            paymentInitialized.getAmount(),
                            paymentInitialized.getStatus().toString(),
                            paymentInitialized.getInitializedAt()
                    );

                    paymentEntryRepository.save(payment);
                }
        );
    }

    @EventHandler
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(@NonNull PaymentCompleted paymentCompleted,
                       @NonNull @MessageIdentifier String eventId) {
        consumeOrSkipIfConsumed(
                eventId,
                () -> {
                    var paymentId = paymentCompleted.getPaymentId();
                    var status = paymentCompleted.getStatus().toString();
                    var completedAt = paymentCompleted.getCompletedAt();

                    var payment = paymentEntryRepository
                            .findById(paymentId)
                            .orElseThrow(() -> new IllegalStateException("Could not find payment " + paymentId));
                    payment.completePayment(status, completedAt);

                    paymentEntryRepository.save(payment);

                    var paymentFinalized = new PaymentFinalized(
                            paymentId,
                            payment.getOrderId(),
                            payment.getAmount(),
                            completedAt
                    );

                    eventSender.sendPublicEvent(paymentFinalized);
                }
        );
    }

    @EventHandler
    @Transactional(propagation = Propagation.REQUIRED)
    public void handle(@NonNull PaymentDiscarded paymentDiscarded,
                       @NonNull @MessageIdentifier String eventId) {
        consumeOrSkipIfConsumed(
                eventId,
                () -> {
                    var paymentId = paymentDiscarded.getPaymentId();
                    var status = paymentDiscarded.getStatus().toString();
                    var discardedAt = paymentDiscarded.getDiscardedAt();

                    var payment = paymentEntryRepository
                            .findById(paymentId)
                            .orElseThrow(() -> new IllegalStateException("Could not find payment " + paymentId));
                    payment.discardPayment(status, discardedAt);

                    paymentEntryRepository.save(payment);

                    var paymentFinalized = new PaymentFailed(
                            paymentId,
                            payment.getOrderId(),
                            payment.getAmount(),
                            discardedAt
                    );

                    eventSender.sendPublicEvent(paymentFinalized);
                }
        );
    }

    private void consumeOrSkipIfConsumed(String eventId,
                                         Runnable action) {
        idempotentEventProcessorService.processEvent(UUID.fromString(eventId), CONTEXT, action);
    }

}
