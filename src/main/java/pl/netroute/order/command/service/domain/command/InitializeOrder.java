package pl.netroute.order.command.service.domain.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import pl.netroute.command.Command;
import pl.netroute.order.command.service.domain.ProductItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Value
public class InitializeOrder implements Command {

    @TargetAggregateIdentifier
    UUID orderId;

    UUID clientId;

    List<ProductItem> products;
    BigDecimal totalPrice;

}
