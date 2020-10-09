package pl.netroute.cart.query.repository;

import org.springframework.data.repository.CrudRepository;
import pl.netroute.cart.query.repository.domain.ProductEntry;
import pl.netroute.cart.query.repository.domain.ProductEntryId;

public interface ProductEntryRepository extends CrudRepository<ProductEntry, ProductEntryId> {
}
