package pl.netroute.cart.query.repository;

import org.springframework.data.repository.CrudRepository;
import pl.netroute.cart.query.repository.domain.CartEntry;

import java.util.UUID;

public interface CartEntryRepository extends CrudRepository<CartEntry, UUID> {
}
