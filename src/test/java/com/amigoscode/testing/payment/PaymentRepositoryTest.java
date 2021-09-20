package com.amigoscode.testing.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})
class PaymentRepositoryTest {

    @Autowired private PaymentRepository paymentRepository;

    @Test
    void itShouldInsertPayment() {
        long paymentId = 1L;
        Payment payment = new Payment(
                paymentId,
                UUID.randomUUID(),
                new BigDecimal("10.00"),
                Currency.USD, "card123",
                "donation");

        //When
        paymentRepository.save(payment);

        //Then
        Optional<Payment> paymentOptional = paymentRepository.findById(paymentId);
        assertThat(paymentOptional)
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualTo(payment));
    }
}