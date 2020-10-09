package pl.netroute.configuration;

import org.axonframework.eventhandling.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.netroute.event.EventSender;

@Configuration
public class EventDispatchingConfiguration {

    @Bean
    EventSender eventSender(EventBus eventBus) {
        return new EventSender(eventBus);
    }

}
