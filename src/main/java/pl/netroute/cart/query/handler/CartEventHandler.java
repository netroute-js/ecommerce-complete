package pl.netroute.cart.query.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.messaging.annotation.MessageIdentifier;
import org.springframework.stereotype.Component;
import pl.netroute.cart.command.service.domain.event.CartConfirmed;
import pl.netroute.cart.command.service.domain.event.CartInitialized;
import pl.netroute.cart.command.service.domain.event.ProductAdded;
import pl.netroute.cart.command.service.domain.event.ProductRemoved;
import pl.netroute.cart.external.event.CartFinalized;
import pl.netroute.cart.query.repository.CartEntryRepository;
import pl.netroute.cart.query.repository.ProductEntryRepository;
import pl.netroute.cart.query.repository.domain.CartEntry;
import pl.netroute.event.EventSender;
import pl.netroute.event.processor.service.IdempotentEventProcessorService;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CartEventHandler {
    private static final String CONTEXT = "CartView";

    private final EventSender eventSender;
    private final CartEntryRepository cartEntryRepository;
    private final IdempotentEventProcessorService idempotentEventProcessorService;

    @EventHandler
    public void handle(@NonNull CartInitialized cartInitialized,
                       @NonNull @MessageIdentifier String eventId) {
        consumeOrSkipIfConsumed(
                eventId,
                () -> {
                    log.info("Handling {}", cartInitialized);

                    var cart = new CartEntry();
                    cart.initializeCart(
                            cartInitialized.getCartId(),
                            cartInitialized.getClientId(),
                            cartInitialized.getStatus().toString()
                    );

                    cartEntryRepository.save(cart);
                }
        );
    }

    @EventHandler
    public void handle(@NonNull ProductAdded productAdded,
                       @NonNull @MessageIdentifier String eventId) {
        consumeOrSkipIfConsumed(
                eventId,
                () -> {
                    log.info("Handling {}", productAdded);

                    var product = productAdded.getProduct();
                    var cartId = productAdded.getCartId();
                    var cart = cartEntryRepository
                            .findById(cartId)
                            .orElseThrow(() -> new IllegalStateException("Could not find Cart " + cartId));

                    cart.addProduct(product);
                    cartEntryRepository.save(cart);
                }
        );
    }

    @EventHandler
    public void handle(@NonNull ProductRemoved productRemoved,
                       @NonNull @MessageIdentifier String eventId) {
        consumeOrSkipIfConsumed(
                eventId,
                () -> {
                    log.info("Handling {}", productRemoved);

                    var product = productRemoved.getProduct();
                    var cartId = productRemoved.getCartId();
                    var cart = cartEntryRepository
                            .findById(cartId)
                            .orElseThrow(() -> new IllegalStateException("Could not find Cart " + cartId));

                    cart.removeProduct(product);
                    cartEntryRepository.save(cart);
                }
        );
    }

    @EventHandler
    public void handle(@NonNull CartConfirmed cartConfirmed,
                       @NonNull @MessageIdentifier String eventId) {
        consumeOrSkipIfConsumed(
                eventId,
                () -> {
                    log.info("Handling {}", cartConfirmed);

                    var cartId = cartConfirmed.getCartId();
                    var cart = cartEntryRepository
                            .findById(cartId)
                            .orElseThrow(() -> new IllegalStateException("Could not find Cart " + cartId));

                    var confirmedAt = cartConfirmed.getConfirmedAt();
                    cart.confirmCart(
                            cartConfirmed.getStatus().toString(),
                            confirmedAt
                    );

                    cartEntryRepository.save(cart);

                    var products = cart
                            .getProducts()
                            .stream()
                            .map(productEntry -> new CartFinalized.Product(
                                    productEntry.getProductId().getProductId(),
                                    productEntry.getProductId().getPrice(),
                                    productEntry.getQuantity()))
                            .collect(Collectors.toUnmodifiableList());

                    var cartFinalized = new CartFinalized(
                            cartId,
                            cart.getClientId(),
                            products,
                            cartConfirmed.getTotalPrice(),
                            confirmedAt
                    );

                    eventSender.sendPublicEvent(cartFinalized);
                }
        );
    }

    private void consumeOrSkipIfConsumed(String eventId,
                                         Runnable action) {
        idempotentEventProcessorService.processEvent(UUID.fromString(eventId), CONTEXT, action);
    }

}
