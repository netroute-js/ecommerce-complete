package pl.netroute.event.processor.repository;

import org.springframework.data.repository.CrudRepository;
import pl.netroute.event.processor.repository.domain.ProcessedEntryId;
import pl.netroute.event.processor.repository.domain.ProcessedEventEntry;

public interface ProcessedEventEntryRepository extends CrudRepository<ProcessedEventEntry, ProcessedEntryId> {
}
