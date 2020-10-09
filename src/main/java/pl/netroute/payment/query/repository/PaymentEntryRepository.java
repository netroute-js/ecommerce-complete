package pl.netroute.payment.query.repository;

import lombok.NonNull;
import org.springframework.data.repository.CrudRepository;
import pl.netroute.payment.query.repository.domain.PaymentEntry;

import java.util.Optional;
import java.util.UUID;

public interface PaymentEntryRepository extends CrudRepository<PaymentEntry, UUID> {
    Optional<PaymentEntry> findByOrderId(@NonNull UUID orderId);
}
