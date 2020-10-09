package pl.netroute.payment.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.event.PrivateEvent;
import pl.netroute.payment.command.service.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Revision("1.0")
public class PaymentInitialized implements PrivateEvent {
    UUID paymentId;
    UUID orderId;
    PaymentStatus status;
    BigDecimal amount;
    Instant initializedAt;
}
