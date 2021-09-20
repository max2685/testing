package com.amigoscode.testing.payment;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRegistrationController;
import com.amigoscode.testing.customer.CustomerRegistrationRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentsIntegrationTest {


    @Autowired private PaymentRepository paymentRepository;

    @Autowired private MockMvc mockMvc;


//    This only tests method, but we need to test api
//    @Autowired
//    private CustomerRegistrationController customerRegistrationController;

    @Test
    void itShouldCreatePaymentSuccessfully() throws Exception {

        //Given a cystomer
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(customerId, "James", "+440000000");
        CustomerRegistrationRequest customerRegistrationRequest = new CustomerRegistrationRequest(customer);

        //register
        ResultActions customerRegResultActions = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customer-registration")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(customerRegistrationRequest))));

        //payment
        long paymentId = 1L;

        Payment payment = new Payment(
                paymentId,
                customerId,
                new BigDecimal("100.00"),
                Currency.GBP, "x0x0x0x0",
                "zakat");


        //payment request
        PaymentRequest paymentRequest = new PaymentRequest(payment);

        //when payment is sent
        ResultActions paymentRegResultActions = mockMvc.perform(post("/api/v1/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(Objects.requireNonNull(objectToJson(paymentRequest))));

        //then both customer register and payment requests are 200
        paymentRegResultActions.andExpect(status().isOk());
        customerRegResultActions.andExpect(status().isOk());

        //payment is stored in db
        //TODO do not use payment repository. Instead create an endpoint to retrieve payments for customers
        assertThat(paymentRepository.findById(paymentId))
                .isPresent()
                .hasValueSatisfying(p -> assertThat(p).isEqualToComparingFieldByField(payment));

        //TODO ensure sms is delivered
    }

    private String objectToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            fail("Failed to convert object to json");
            return null;
        }
    }
}
