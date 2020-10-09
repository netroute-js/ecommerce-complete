package pl.netroute.shipment.external.event;

import lombok.Value;
import pl.netroute.event.PublicEvent;

import java.util.UUID;

@Value
public class PackageSent implements PublicEvent {
    UUID orderId;
    UUID packageId;
}
