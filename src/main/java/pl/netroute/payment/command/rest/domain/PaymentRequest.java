package pl.netroute.payment.command.rest.domain;

import lombok.Value;

import java.math.BigDecimal;
import java.util.UUID;

@Value
public class PaymentRequest {
    UUID orderId;
    BigDecimal amount;
}
