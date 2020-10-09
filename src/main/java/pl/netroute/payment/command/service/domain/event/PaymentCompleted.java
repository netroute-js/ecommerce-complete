package pl.netroute.payment.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.event.PrivateEvent;
import pl.netroute.payment.command.service.domain.PaymentStatus;

import java.time.Instant;
import java.util.UUID;

@Value
@Revision("1.0")
public class PaymentCompleted implements PrivateEvent {
    UUID paymentId;
    PaymentStatus status;
    Instant completedAt;
}
