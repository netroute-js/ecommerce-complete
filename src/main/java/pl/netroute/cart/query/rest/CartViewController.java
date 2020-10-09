package pl.netroute.cart.query.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.netroute.cart.query.repository.CartEntryRepository;
import pl.netroute.cart.query.repository.domain.ProductEntry;
import pl.netroute.cart.query.rest.domain.CartResource;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/query/cart")
@RequiredArgsConstructor
public class CartViewController {
    private final CartEntryRepository cartEntryRepository;

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResource> getCartDetails(@PathVariable UUID cartId) {
        log.info("Getting cart[{}] details", cartId);

        var maybeCart = cartEntryRepository
                .findById(cartId)
                .map(cartEntry -> new CartResource(
                        cartEntry.getId(),
                        cartEntry.getClientId(),
                        cartEntry.getStatus(),
                        cartEntry
                                .getProducts()
                                .stream()
                                .map(this::mapToDomain)
                                .collect(Collectors.toUnmodifiableList()),
                        cartEntry.getConfirmedAt()
                ));

        return ResponseEntity.of(maybeCart);
    }

    private CartResource.ProductResource mapToDomain(ProductEntry productEntry) {
        return new CartResource.ProductResource(
                productEntry.getProductId().getProductId(),
                productEntry.getProductId().getPrice(),
                productEntry.getQuantity());
    }
}
