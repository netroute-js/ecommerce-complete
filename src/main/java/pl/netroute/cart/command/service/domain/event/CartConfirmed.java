package pl.netroute.cart.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.cart.command.service.domain.CartStatus;
import pl.netroute.event.PrivateEvent;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Value
@Revision("2.0")
public class CartConfirmed implements PrivateEvent {
    UUID cartId;
    BigDecimal totalPrice;
    BigDecimal bonusPrice;
    CartStatus status;
    Instant confirmedAt;
}
