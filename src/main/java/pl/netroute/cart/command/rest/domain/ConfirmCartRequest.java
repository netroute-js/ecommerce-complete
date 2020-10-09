package pl.netroute.cart.command.rest.domain;

import lombok.Value;

import java.util.UUID;

@Value
public class ConfirmCartRequest {
    UUID cartId;
}
