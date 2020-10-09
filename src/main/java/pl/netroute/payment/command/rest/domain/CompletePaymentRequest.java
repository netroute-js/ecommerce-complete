package pl.netroute.payment.command.rest.domain;

import lombok.Value;

import java.util.UUID;

@Value
public class CompletePaymentRequest {
    UUID paymentId;
}
