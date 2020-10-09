package pl.netroute.cart.command.service.domain.command;

import lombok.Value;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import pl.netroute.cart.command.service.domain.ProductItem;
import pl.netroute.command.Command;

import java.util.UUID;

@Value
public class AddProduct implements Command {

    @TargetAggregateIdentifier
    UUID cartId;

    ProductItem product;
}
