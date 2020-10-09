package pl.netroute.cart.command.service.domain.exception;

public class NotEnoughProductQuantityException extends RuntimeException {

    public NotEnoughProductQuantityException(String message) {
        super(message);
    }

    public NotEnoughProductQuantityException(String message, Throwable cause) {
        super(message, cause);
    }

}
