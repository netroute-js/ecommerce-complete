package pl.netroute.payment.command.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.netroute.command.CommandSender;
import pl.netroute.payment.command.rest.domain.CompletePaymentRequest;
import pl.netroute.payment.command.rest.domain.DiscardPaymentRequest;
import pl.netroute.payment.command.service.domain.command.CompletePayment;
import pl.netroute.payment.command.service.domain.command.DiscardPayment;

@Slf4j
@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final CommandSender commandSender;

    @PostMapping(path = "/complete")
    public ResponseEntity<Void> completePayment(@RequestBody CompletePaymentRequest request) {
        log.info("Complete payment {}", request);

        var completePayment = new CompletePayment(request.getPaymentId());
        commandSender.sendSyncCommand(completePayment);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/discard")
    public ResponseEntity<Void> discardPayment(@RequestBody DiscardPaymentRequest request) {
        log.info("Discard payment {}", request);

        var discardPayment = new DiscardPayment(request.getPaymentId(), request.getDiscardReason());
        commandSender.sendSyncCommand(discardPayment);

        return ResponseEntity.ok().build();
    }

}
