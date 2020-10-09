package pl.netroute.payment.external.event;

import lombok.Value;
import pl.netroute.event.PublicEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
public class PaymentFinalized implements PublicEvent {
    UUID paymentId;
    UUID orderId;
    BigDecimal amount;
    Instant finalizedAt;
}
