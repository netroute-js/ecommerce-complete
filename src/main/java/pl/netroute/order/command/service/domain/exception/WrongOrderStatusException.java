package pl.netroute.order.command.service.domain.exception;

public class WrongOrderStatusException extends RuntimeException {

    public WrongOrderStatusException(String message) {
        super(message);
    }

    public WrongOrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }

}
