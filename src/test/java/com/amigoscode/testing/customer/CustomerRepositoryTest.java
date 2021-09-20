package com.amigoscode.testing.customer;

import com.amigoscode.testing.customer.Customer;
import com.amigoscode.testing.customer.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

//properties will show what validation should have fields of entity
@DataJpaTest(properties = {
        "spring.jpa.properties.javax.persistence.validation.mode=none"
})

public class CustomerRepositoryTest {

    @Autowired private CustomerRepository customerRepository;

    @Test
    void itShouldSelectCustomerByPhoneNumberTest() {
        //given
        String phoneNumber = "+4402312412";

        //when
        Optional<Customer> optionalCustomer = customerRepository.selectCustomerByPhoneNumber(phoneNumber);

        //then
        assertThat(optionalCustomer).isNotPresent();
    }

    @Test
    void itShouldNotSelectCustomerByPhoneNumberWhenPhoneExists() {
        //given
        UUID id = UUID.randomUUID();
        String phoneNumber = "+4402312412";
        Customer customer = new Customer(id, "Max", phoneNumber);

        //when
        customerRepository.save(customer);

        //then
        Optional<Customer> optionalCustomer = customerRepository.selectCustomerByPhoneNumber(phoneNumber);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldSaveCustomer() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Max", "+44243524");
        customerRepository.save(customer);
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        assertThat(optionalCustomer)
                .isPresent()
                .hasValueSatisfying(c -> {
//                    assertThat(c.getId()).isEqualTo(id);
//                    assertThat(c.getName()).isEqualTo("Max");
//                    assertThat(c.getPhoneNumber()).isEqualTo("243524");
                    assertThat(c).isEqualToComparingFieldByField(customer);
                });
    }

    @Test
    void itShouldNotSaveCustomerWhenNameIsNull() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, null, "+44243524");

        assertThatThrownBy(() -> customerRepository.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.name")
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void itShouldNotSaveCustomerWhenPhoneNumberIsNull() {
        UUID id = UUID.randomUUID();
        Customer customer = new Customer(id, "Alex", null);

        assertThatThrownBy(() -> customerRepository.save(customer))
                .hasMessageContaining("not-null property references a null or transient value : com.amigoscode.testing.customer.Customer.phoneNumber")
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
