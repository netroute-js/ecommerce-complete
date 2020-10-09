package pl.netroute.order.command.service.domain.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import pl.netroute.command.Command;

import java.util.UUID;

@Value
public class CancelOrder implements Command {

    @TargetAggregateIdentifier
    UUID orderId;

    String reason;

}
