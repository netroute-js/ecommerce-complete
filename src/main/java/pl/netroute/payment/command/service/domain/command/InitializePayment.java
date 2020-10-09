package pl.netroute.payment.command.service.domain.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import pl.netroute.command.Command;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class InitializePayment implements Command {

    @TargetAggregateIdentifier
    UUID paymentId;

    UUID orderId;
    BigDecimal amount;
}
