package pl.netroute.order.command.service.domain;

import lombok.Value;

import java.util.UUID;

@Value
public class ProductItem {
    UUID productId;
    int quantity;
}
