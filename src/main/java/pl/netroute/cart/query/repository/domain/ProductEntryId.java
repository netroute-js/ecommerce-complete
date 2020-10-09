package pl.netroute.cart.query.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntryId implements Serializable {

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID productId;

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID cartId;

    private BigDecimal price;

    public BigDecimal getPrice() {
        return price.setScale(2);
    }

}
