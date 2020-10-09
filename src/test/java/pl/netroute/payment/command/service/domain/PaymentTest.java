package pl.netroute.payment.command.service.domain;

import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pl.netroute.payment.command.service.domain.command.CompletePayment;
import pl.netroute.payment.command.service.domain.command.DiscardPayment;
import pl.netroute.payment.command.service.domain.command.InitializePayment;
import pl.netroute.payment.command.service.domain.event.PaymentCompleted;
import pl.netroute.payment.command.service.domain.event.PaymentDiscarded;
import pl.netroute.payment.command.service.domain.event.PaymentInitialized;
import pl.netroute.payment.command.service.domain.exception.WrongPaymentStatusException;
import pl.netroute.payment.command.service.domain.handler.PaymentCommandHandler;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

public class PaymentTest {
    private Clock fixedClock = Clock.fixed(Instant.now(), ZoneId.of("UTC"));

    private FixtureConfiguration<Payment> paymentFixture;

    @BeforeEach
    public void setup() {
        paymentFixture = new AggregateTestFixture<>(Payment.class);

        var paymentCommandHandler = new PaymentCommandHandler(
                fixedClock,
                paymentFixture.getRepository()
        );
        paymentFixture.registerAnnotatedCommandHandler(paymentCommandHandler);
    }

    @Test
    public void shouldInitializePayment() {
        // given
        var initializePayment = new InitializePayment(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.TEN
        );

        // when
        // then
        var paymentInitialized = new PaymentInitialized(
                initializePayment.getPaymentId(),
                initializePayment.getOrderId(),
                PaymentStatus.PENDING,
                initializePayment.getAmount(),
                fixedClock.instant()
        );

        paymentFixture
                .givenNoPriorActivity()
                .when(initializePayment)
                .expectEvents(paymentInitialized);
    }

    @Test
    public void shouldFailCompletingNotPendingPayment() {
        // given
        var paymentInitialized = new PaymentInitialized(
                UUID.randomUUID(),
                UUID.randomUUID(),
                PaymentStatus.PENDING,
                new BigDecimal(BigInteger.TEN),
                fixedClock.instant()
        );

        var paymentCompleted = new PaymentCompleted(
                paymentInitialized.getPaymentId(),
                PaymentStatus.COMPLETED,
                fixedClock.instant()
        );

        var completePayment = new CompletePayment(
                paymentInitialized.getPaymentId()
        );

        // when
        // then
        paymentFixture
                .given(paymentInitialized, paymentCompleted)
                .when(completePayment)
                .expectException(WrongPaymentStatusException.class);
    }

    @Test
    public void shouldCompletePayment() {
        // given
        var paymentInitialized = new PaymentInitialized(
                UUID.randomUUID(),
                UUID.randomUUID(),
                PaymentStatus.PENDING,
                new BigDecimal(BigInteger.TEN),
                fixedClock.instant()
        );

        var completePayment = new CompletePayment(
                paymentInitialized.getPaymentId()
        );

        // when
        // then
        var paymentCompleted = new PaymentCompleted(
                paymentInitialized.getPaymentId(),
                PaymentStatus.COMPLETED,
                fixedClock.instant()
        );

        paymentFixture
                .given(paymentInitialized)
                .when(completePayment)
                .expectEvents(paymentCompleted);
    }

    @Test
    public void shouldSkipDiscardingAlreadyDiscardedPayment() {
        // given
        var paymentInitialized = new PaymentInitialized(
                UUID.randomUUID(),
                UUID.randomUUID(),
                PaymentStatus.PENDING,
                new BigDecimal(BigInteger.TEN),
                fixedClock.instant()
        );

        var paymentDiscarded = new PaymentDiscarded(
                paymentInitialized.getPaymentId(),
                PaymentStatus.DISCARDED,
                "Cancelled by customer",
                fixedClock.instant()
        );

        var discardPayment = new DiscardPayment(
                paymentInitialized.getPaymentId(),
                "Not enough money"
        );

        // when
        // then
        paymentFixture
                .given(paymentInitialized, paymentDiscarded)
                .when(discardPayment)
                .expectNoEvents();
    }

    @Test
    public void shouldDiscardPayment() {
        // given
        var paymentInitialized = new PaymentInitialized(
                UUID.randomUUID(),
                UUID.randomUUID(),
                PaymentStatus.PENDING,
                new BigDecimal(BigInteger.TEN),
                fixedClock.instant()
        );

        var discardPayment = new DiscardPayment(
                paymentInitialized.getPaymentId(),
                "Not enough money"
        );

        // when
        // then
        var paymentDiscarded = new PaymentDiscarded(
                paymentInitialized.getPaymentId(),
                PaymentStatus.DISCARDED,
                discardPayment.getReason(),
                fixedClock.instant()
        );

        paymentFixture
                .given(paymentInitialized)
                .when(discardPayment)
                .expectEvents(paymentDiscarded);
    }

}
