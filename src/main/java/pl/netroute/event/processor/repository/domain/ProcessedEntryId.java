package pl.netroute.event.processor.repository.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.UUID;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedEntryId implements Serializable {

    @Type(type="org.hibernate.type.UUIDCharType")
    private UUID eventId;
    private String context;

}
