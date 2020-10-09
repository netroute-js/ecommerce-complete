package pl.netroute.cart.command.service.domain.handler;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.modelling.command.Repository;
import pl.netroute.cart.command.service.BonusPriceService;
import pl.netroute.cart.command.service.ProductPriceService;
import pl.netroute.cart.command.service.domain.Cart;
import pl.netroute.cart.command.service.domain.command.AddProduct;
import pl.netroute.cart.command.service.domain.command.ConfirmCart;
import pl.netroute.cart.command.service.domain.command.InitializeCart;
import pl.netroute.cart.command.service.domain.command.RemoveProduct;

import java.time.Clock;

@RequiredArgsConstructor
public class CartCommandHandler {
    private final Clock clock;
    private final Repository<Cart> cartRepository;
    private final BonusPriceService bonusPriceService;
    private final ProductPriceService productPriceService;

    @CommandHandler
    public void handle(@NonNull InitializeCart initializeCart) throws Exception {
        cartRepository.newInstance(() -> new Cart(initializeCart));
    }

    @CommandHandler
    public void handle(@NonNull AddProduct addProduct) {
        var product = addProduct.getProduct();

        cartRepository
                .load(addProduct.getCartId().toString())
                .execute(cart -> cart.addProduct(product, productPriceService));
    }

    @CommandHandler
    public void handle(@NonNull RemoveProduct removeProduct) {
        var product = removeProduct.getProduct();

        cartRepository
                .load(removeProduct.getCartId().toString())
                .execute(cart -> cart.removeProduct(product));
    }

    @CommandHandler
    public void handle(@NonNull ConfirmCart confirmCart) {
        var cartId = confirmCart.getCartId();
        var bonusPrice = bonusPriceService.calculateBonus(cartId);

        cartRepository
                .load(cartId.toString())
                .execute(cart -> cart.confirm(
                            bonusPrice,
                            clock
                        )
                );
    }

}
