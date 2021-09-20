package com.amigoscode.testing.customer;

import com.amigoscode.testing.util.PhoneNumberValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class CustomerRegistrationServiceTest {
    @Mock private CustomerRepository customerRepository;
    @Mock private PhoneNumberValidator phoneNumberValidator;
    @Captor private ArgumentCaptor<Customer> customerArgumentCaptor;
    private CustomerRegistrationService customerRegistrationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        customerRegistrationService = new CustomerRegistrationService(customerRepository, phoneNumberValidator);
    }

    @Test
    void itShouldSaveNewCustomer() {
        // given a phone number and a customer
        String phoneNumber = "+444443524365";
        Customer customer = new Customer(UUID.randomUUID(), "Max", phoneNumber);

        //a request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        //no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //when
        customerRegistrationService.registerNewCustomer(registrationRequest);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualTo(customer);
    }

    @Test
    void itShouldSaveNewCustomerWhenIdIsNull() {
        // given a phone number and a customer
        String phoneNumber = "+4444565787";
        Customer customer = new Customer(null, "Max", phoneNumber);

        //a request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        //no customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.empty());

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //when
        customerRegistrationService.registerNewCustomer(registrationRequest);

        //then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue).isEqualToIgnoringGivenFields(customer, "id");
        assertThat(customerArgumentCaptorValue.getId()).isNotNull();
    }

    @Test
    void itShouldNotSaveNewCustomer(){
        // given a phone number and a customer
        String phoneNumber = "+444454678";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Max", phoneNumber);

        //a request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        //existing customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customer));

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //when
        customerRegistrationService.registerNewCustomer(registrationRequest);

        //then
        then(customerRepository).should(never()).save(any());
    }

    @Test
    void itShouldThrowExceptionWhenPhoneNumberIsTaken() {
        // given a phone number and a customer
        String phoneNumber = "+44000099";
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Max", phoneNumber);
        Customer customerTwo = new Customer(id, "John", phoneNumber);


        //a request
        CustomerRegistrationRequest registrationRequest = new CustomerRegistrationRequest(customer);

        //existing customer with phone number passed
        given(customerRepository.selectCustomerByPhoneNumber(phoneNumber)).willReturn(Optional.of(customerTwo));

        given(phoneNumberValidator.test(phoneNumber)).willReturn(true);

        //when
        //then
        assertThatThrownBy(() -> customerRegistrationService.registerNewCustomer(registrationRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(String.format("phone number: [%s] is taken", phoneNumber));

        //finally
        then(customerRepository).should(never()).save(any(Customer.class));
    }
}