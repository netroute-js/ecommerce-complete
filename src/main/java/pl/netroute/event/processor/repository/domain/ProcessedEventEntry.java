package pl.netroute.event.processor.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEventEntry {

    @EmbeddedId
    private ProcessedEntryId processedEntryId;

}
