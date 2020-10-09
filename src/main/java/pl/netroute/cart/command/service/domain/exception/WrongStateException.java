package pl.netroute.cart.command.service.domain.exception;

public class WrongStateException extends RuntimeException {

    public WrongStateException(String message) {
        super(message);
    }

    public WrongStateException(String message, Throwable cause) {
        super(message, cause);
    }

}
