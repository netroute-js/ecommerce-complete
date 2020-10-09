package pl.netroute.payment.command.service.domain.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.Repository;
import pl.netroute.payment.command.service.domain.Payment;
import pl.netroute.payment.command.service.domain.command.CompletePayment;
import pl.netroute.payment.command.service.domain.command.DiscardPayment;
import pl.netroute.payment.command.service.domain.command.InitializePayment;

import java.time.Clock;

@RequiredArgsConstructor
public class PaymentCommandHandler {
    private final Clock clock;
    private final Repository<Payment> paymentRepository;

    @CommandHandler
    public void handle(@NonNull InitializePayment initializePayment) throws Exception {
        paymentRepository.newInstance(() -> new Payment(initializePayment, clock));
    }

    @CommandHandler
    public void handle(@NonNull CompletePayment completePayment) {
        paymentRepository
                .load(completePayment.getPaymentId().toString())
                .execute(payment -> payment.complete(completePayment, clock));
    }

    @CommandHandler
    public void handle(@NonNull DiscardPayment discardPayment) {
        paymentRepository
                .load(discardPayment.getPaymentId().toString())
                .execute(payment -> payment.discard(discardPayment, clock));
    }

}
