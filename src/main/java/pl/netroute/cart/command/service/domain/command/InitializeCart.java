package pl.netroute.cart.command.service.domain.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import pl.netroute.command.Command;

import java.util.UUID;

@Value
public class InitializeCart implements Command {

    @TargetAggregateIdentifier
    UUID cartId;

    UUID clientId;

}
