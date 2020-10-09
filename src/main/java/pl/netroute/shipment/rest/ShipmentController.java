package pl.netroute.shipment.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.netroute.event.EventSender;
import pl.netroute.event.PublicEvent;
import pl.netroute.shipment.external.event.PackageRejected;
import pl.netroute.shipment.external.event.PackageSent;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/shipment")
@RequiredArgsConstructor
public class ShipmentController {
    private final EventSender eventSender;

    @PostMapping(path = "/send/{orderId}")
    public ResponseEntity<Void> sendShipment(@PathVariable UUID orderId,
                                             @RequestParam boolean success) {
        UUID packageId = UUID.randomUUID();
        PublicEvent packageEvent = null;

        if(success) {
            packageEvent = new PackageSent(
                    orderId,
                    packageId
            );
        } else {
            packageEvent = new PackageRejected(
                    orderId,
                    packageId
            );
        }

        eventSender.sendPublicEvent(packageEvent);

        return ResponseEntity.accepted().build();
    }

}
