package pl.netroute.order.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.event.PrivateEvent;
import pl.netroute.order.command.service.domain.OrderStatus;

import java.time.Instant;
import java.util.UUID;

@Value
@Revision("1.0")
public class OrderCancelled implements PrivateEvent {
    UUID orderId;
    OrderStatus status;
    String reason;
    Instant cancelledAt;
}
