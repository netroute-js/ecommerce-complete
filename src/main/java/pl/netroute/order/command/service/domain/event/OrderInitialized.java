package pl.netroute.order.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.event.PrivateEvent;
import pl.netroute.order.command.service.domain.OrderStatus;
import pl.netroute.order.command.service.domain.ProductItem;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Value
@Revision("1.0")
public class OrderInitialized implements PrivateEvent {
    UUID orderId;
    UUID clientId;
    List<ProductItem> products;
    OrderStatus status;
    BigDecimal totalPrice;
    Instant initializedAt;
}
