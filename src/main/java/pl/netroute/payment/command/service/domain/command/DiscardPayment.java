package pl.netroute.payment.command.service.domain.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import pl.netroute.command.Command;

import java.util.UUID;

@Value
public class DiscardPayment implements Command {

    @TargetAggregateIdentifier
    UUID paymentId;

    String reason;

}
