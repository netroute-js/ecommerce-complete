package pl.netroute.cart.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.cart.command.service.domain.CartStatus;
import pl.netroute.event.PrivateEvent;

import java.util.UUID;

@Value
@Revision("1.0")
public class CartInitialized implements PrivateEvent {
    UUID cartId;
    UUID clientId;
    CartStatus status;
}
