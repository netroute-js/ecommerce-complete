package pl.netroute.payment.command.configuration;

import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.netroute.payment.command.service.domain.Payment;
import pl.netroute.payment.command.service.domain.handler.PaymentCommandHandler;

import java.time.Clock;

@Configuration
public class PaymentApplicationConfiguration {

    @Bean
    PaymentCommandHandler paymentCommandHandler(Clock clock,
                                                AxonConfiguration axonConfiguration) {
        return new PaymentCommandHandler(clock, axonConfiguration.repository(Payment.class));
    }

}
