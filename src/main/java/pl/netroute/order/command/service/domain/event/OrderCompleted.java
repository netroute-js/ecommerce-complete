package pl.netroute.order.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.event.PrivateEvent;
import pl.netroute.order.command.service.domain.OrderStatus;

import java.time.Instant;
import java.util.UUID;

@Value
@Revision("1.0")
public class OrderCompleted implements PrivateEvent {
    UUID orderId;
    OrderStatus status;
    Instant completedAt;
}
