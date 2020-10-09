package pl.netroute.payment.command.service.domain.exception;

public class WrongPaymentStatusException extends RuntimeException {

    public WrongPaymentStatusException(String message) {
        super(message);
    }

    public WrongPaymentStatusException(String message, Throwable cause) {
        super(message, cause);
    }

}
