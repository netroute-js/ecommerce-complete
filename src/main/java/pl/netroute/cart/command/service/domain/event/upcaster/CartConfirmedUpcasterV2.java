package pl.netroute.cart.command.service.domain.event.upcaster;

import lombok.extern.slf4j.Slf4j;
import org.axonframework.serialization.SimpleSerializedType;
import org.axonframework.serialization.upcasting.event.IntermediateEventRepresentation;
import org.axonframework.serialization.upcasting.event.SingleEventUpcaster;
import pl.netroute.cart.command.service.domain.event.CartConfirmed;

import java.math.BigDecimal;

@Slf4j
public class CartConfirmedUpcasterV2 extends SingleEventUpcaster {

    private static SimpleSerializedType TARGET_TYPE =
            new SimpleSerializedType(CartConfirmed.class.getTypeName(), "1.0");

    @Override
    protected boolean canUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        return intermediateRepresentation.getType().equals(TARGET_TYPE);
    }

    @Override
    protected IntermediateEventRepresentation doUpcast(IntermediateEventRepresentation intermediateRepresentation) {
        log.info("Upcasting {}", intermediateRepresentation.getType());

        return intermediateRepresentation.upcastPayload(
                new SimpleSerializedType(CartConfirmed.class.getTypeName(), "2.0"),
                org.dom4j.Document.class,
                document -> {
                    document.getRootElement()
                            .addElement("bonusPrice")
                            .setText(BigDecimal.ZERO.toString());
                    return document;
                }
        );
    }

}
