package pl.netroute.cart.command.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.netroute.cart.command.rest.domain.AddProductRequest;
import pl.netroute.cart.command.rest.domain.InitializeCartRequest;
import pl.netroute.cart.command.rest.domain.RemoveProductRequest;
import pl.netroute.cart.command.service.domain.command.AddProduct;
import pl.netroute.cart.command.service.domain.command.ConfirmCart;
import pl.netroute.cart.command.service.domain.command.InitializeCart;
import pl.netroute.cart.command.service.domain.command.RemoveProduct;
import pl.netroute.command.CommandSender;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
    private final CommandSender commandSender;

    @PutMapping(path = "/init")
    public ResponseEntity<UUID> initCart(@RequestBody InitializeCartRequest request) {
        log.info("Initialize cart {}", request);

        var cartId = UUID.randomUUID();
        var initializeCart = new InitializeCart(
                cartId,
                request.getClientId()
        );

        commandSender.sendSyncCommand(initializeCart);

        return ResponseEntity.ok(cartId);
    }

    @PostMapping(path = "/confirm")
    public ResponseEntity<Void> confirmCart(@RequestBody ConfirmCart request) {
        log.info("Confirm cart {}", request);

        var cartId = request.getCartId();
        var confirmCart = new ConfirmCart(cartId);

        commandSender.sendSyncCommand(confirmCart);

        return ResponseEntity.ok().build();
    }

    @PostMapping(path = "/product")
    public ResponseEntity<Void> addProduct(@RequestBody AddProductRequest request) {
        log.info("Add product: {}", request);

        var addProduct = new AddProduct(
                request.getCartId(),
                request.toProduct()
        );

        commandSender.sendSyncCommand(addProduct);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping(path = "/product")
    public ResponseEntity<Void> removeProduct(@RequestBody RemoveProductRequest request) {
        log.info("Remove product: {}", request);

        var removeProduct = new RemoveProduct(
                request.getCartId(),
                request.toPricedProduct()
        );

        commandSender.sendSyncCommand(removeProduct);

        return ResponseEntity.ok().build();
    }

}
