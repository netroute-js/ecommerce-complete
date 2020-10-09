package pl.netroute.payment.query.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Type;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEntry {

    @Id
    @Type(type="org.hibernate.type.UUIDCharType")
    UUID id;

    @Type(type="org.hibernate.type.UUIDCharType")
    UUID orderId;

    BigDecimal amount;
    String status;
    Instant initializedAt;
    Instant lastChangeAt;

    public void initializePayment(@NonNull UUID id,
                                  @NonNull UUID orderId,
                                  @NonNull BigDecimal amount,
                                  @NonNull String status,
                                  @NonNull Instant initializedAt) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
        this.initializedAt = initializedAt;
        this.lastChangeAt = initializedAt;
    }

    public void completePayment(@NonNull String status,
                                @NonNull Instant completedAt) {
        this.status = status;
        this.lastChangeAt = completedAt;
    }

    public void discardPayment(@NonNull String status,
                               @NonNull Instant discardedAt) {
        this.status = status;
        this.lastChangeAt = discardedAt;
    }

}
