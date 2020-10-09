package pl.netroute.configuration;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.netroute.command.CommandSender;

@Configuration
public class CommandDispatchingConfiguration {

    @Bean
    CommandBus commandBus() {
        return SimpleCommandBus
                .builder()
                .build();
    }

    @Bean
    CommandSender commandSender(CommandGateway commandGateway) {
        return new CommandSender(commandGateway);
    }

}
