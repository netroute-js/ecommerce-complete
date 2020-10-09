package pl.netroute.order.command.service.domain.factory;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class OrderIdFactory {

    public UUID newId() {
        return UUID.randomUUID();
    }

}
