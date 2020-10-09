package pl.netroute.cart.query.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;
import pl.netroute.cart.command.service.domain.PricedProductItem;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class CartEntry {

    @Id
    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID id;

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID clientId;

    private String status;
    private Instant confirmedAt;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductEntry> products;

    public void initializeCart(@NonNull UUID id,
                               @NonNull UUID clientId,
                               @NonNull String status) {
        this.id = id;
        this.clientId = clientId;
        this.status = status;
    }

    public void confirmCart(@NonNull String status,
                            @NonNull Instant ConfirmedAt) {
        this.status = status;
        this.confirmedAt = confirmedAt;
    }

    public void addProduct(@NonNull PricedProductItem product) {
        var quantity = product.getQuantity();

        var productEntry = new ProductEntry(
                new ProductEntryId(product.getProductId(), id, product.getUnitPrice()),
                quantity,
                this
        );

        findProduct(productEntry.getProductId())
                .ifPresentOrElse(
                        existingProduct -> existingProduct.increaseQuantity(quantity),
                        () -> products.add(productEntry)
                );
    }

    public void removeProduct(@NonNull PricedProductItem product) {
        var quantity = product.getQuantity();

        var productEntry = new ProductEntry(
                new ProductEntryId(product.getProductId(), id, product.getUnitPrice()),
                quantity,
                this
        );

        var productId = productEntry.getProductId();
        findProduct(productId)
                .ifPresentOrElse(
                        existingProduct -> {
                            if(existingProduct.canDecreaseQuantity(quantity)) {
                                existingProduct.decreaseQuantity(quantity);
                            } else {
                                products.remove(existingProduct);
                            }
                        },
                        () -> { new IllegalStateException("Could not find product " + productId); }
                );
    }

    private Optional<ProductEntry> findProduct(ProductEntryId productId) {
        return products
                .stream()
                .filter(currentProduct -> currentProduct.getProductId().equals(productId))
                .findFirst();
    }
}
