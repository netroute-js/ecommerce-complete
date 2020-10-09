package pl.netroute.cart.command.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProductPriceService {
    private final Map<UUID, BigDecimal> productsMap = Stream
            .of(
                    entry(UUID.fromString("1bec9bac-854c-4d66-9a3a-9a13e3220a5a"), new BigDecimal(5000)),
                    entry(UUID.fromString("2bec9bac-854c-4d66-9a3a-9a13e3220a5a"), new BigDecimal(7000)),
                    entry(UUID.fromString("3bec9bac-854c-4d66-9a3a-9a13e3220a5a"), new BigDecimal(10000)))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    public BigDecimal getProductPrice(@NonNull UUID productId) {
        return Optional.ofNullable(productsMap.get(productId))
                .orElseThrow(() -> new IllegalStateException("Could not find product price: " + productId));
    }

    private <K, V> Map.Entry<K, V> entry(K key,
                                         V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }

}
