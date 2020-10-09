package pl.netroute.event;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.GenericEventMessage;

@RequiredArgsConstructor
public class EventSender {
    private final EventBus eventBus;

    public <T extends PublicEvent> void sendPublicEvent(@NonNull T event) {
        var eventMessage = GenericEventMessage.asEventMessage(event);

        eventBus.publish(eventMessage);
    }

}
