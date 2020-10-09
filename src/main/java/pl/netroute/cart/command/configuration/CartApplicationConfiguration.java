package pl.netroute.cart.command.configuration;

import org.axonframework.eventsourcing.EventCountSnapshotTriggerDefinition;
import org.axonframework.eventsourcing.SnapshotTriggerDefinition;
import org.axonframework.eventsourcing.Snapshotter;
import org.axonframework.spring.config.AxonConfiguration;
import org.axonframework.spring.eventsourcing.SpringAggregateSnapshotterFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.netroute.cart.command.service.BonusPriceService;
import pl.netroute.cart.command.service.ProductPriceService;
import pl.netroute.cart.command.service.domain.Cart;
import pl.netroute.cart.command.service.domain.event.upcaster.CartConfirmedUpcasterV2;
import pl.netroute.cart.command.service.domain.handler.CartCommandHandler;

import java.time.Clock;

@Configuration
public class CartApplicationConfiguration {

    @Bean
    CartConfirmedUpcasterV2 cartConfirmedUpcasterV2() {
        return new CartConfirmedUpcasterV2();
    }

    @Bean
    SpringAggregateSnapshotterFactoryBean snapshotterFactoryBean() {
        return new SpringAggregateSnapshotterFactoryBean();
    }

    @Bean
    SnapshotTriggerDefinition cartSnapshotTrigger(Snapshotter snapshotter) {
        return new EventCountSnapshotTriggerDefinition(snapshotter, 2);
    }

    @Bean
    CartCommandHandler cartCommandHandler(Clock clock,
                                          BonusPriceService bonusPriceService,
                                          ProductPriceService productPriceService,
                                          AxonConfiguration axonConfiguration) {
        return new CartCommandHandler(
                clock,
                axonConfiguration.repository(Cart.class),
                bonusPriceService,
                productPriceService
        );
    }

}
