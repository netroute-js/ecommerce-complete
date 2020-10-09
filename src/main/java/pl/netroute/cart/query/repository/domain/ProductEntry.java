package pl.netroute.cart.query.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntry {

    @EmbeddedId
    private ProductEntryId productId;

    private int quantity;

    @ManyToOne
    private CartEntry cart;

    public void increaseQuantity(int increaseQuantityBy) {
        this.quantity = quantity + increaseQuantityBy;
    }

    public void decreaseQuantity(int decreaseQuantityBy) {
        this.quantity = quantity - decreaseQuantityBy;
    }

    public boolean canDecreaseQuantity(int decreaseQuantityBy) {
        return quantity - decreaseQuantityBy > 0;
    }

}
