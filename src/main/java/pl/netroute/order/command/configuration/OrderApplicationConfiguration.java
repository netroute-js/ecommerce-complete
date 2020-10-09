package pl.netroute.order.command.configuration;

import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.netroute.order.command.service.domain.Order;
import pl.netroute.order.command.service.domain.handler.OrderCommandHandler;

import java.time.Clock;

@Configuration
public class OrderApplicationConfiguration {

    @Bean
    OrderCommandHandler orderCommandHandler(Clock clock,
                                            AxonConfiguration axonConfiguration) {
        return new OrderCommandHandler(clock, axonConfiguration.repository(Order.class));
    }

}
