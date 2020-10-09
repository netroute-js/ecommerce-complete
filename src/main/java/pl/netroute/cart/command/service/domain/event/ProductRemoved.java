package pl.netroute.cart.command.service.domain.event;

import lombok.Value;
import org.axonframework.serialization.Revision;
import pl.netroute.cart.command.service.domain.PricedProductItem;
import pl.netroute.event.PrivateEvent;

import java.util.UUID;

@Value
@Revision("1.0")
public class ProductRemoved implements PrivateEvent {
    UUID cartId;
    PricedProductItem product;
}
