package pl.netroute.event.processor.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.netroute.event.processor.repository.ProcessedEventEntryRepository;
import pl.netroute.event.processor.repository.domain.ProcessedEntryId;
import pl.netroute.event.processor.repository.domain.ProcessedEventEntry;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IdempotentEventProcessorService {
    private final ProcessedEventEntryRepository processedEventEntryRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public void processEvent(@NonNull UUID eventId,
                             @NonNull String context,
                             @NonNull Runnable action) {
        var id = new ProcessedEntryId(eventId, context);

        if(processedEventEntryRepository.existsById(id)) {
            return;
        }

        var processedEvent = new ProcessedEventEntry(id);
        processedEventEntryRepository.save(processedEvent);

        action.run();
    }

}
