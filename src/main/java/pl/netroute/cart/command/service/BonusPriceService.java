package pl.netroute.cart.command.service;

import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class BonusPriceService {

    public BigDecimal calculateBonus(@NonNull UUID cartId) {
        return BigDecimal.ONE;
    }

}
