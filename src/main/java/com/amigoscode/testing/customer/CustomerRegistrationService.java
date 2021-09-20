package com.amigoscode.testing.customer;

import com.amigoscode.testing.util.PhoneNumberValidator;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.UUID.randomUUID;

@Service
@AllArgsConstructor
public class CustomerRegistrationService {
    private final CustomerRepository customerRepository;
    private final PhoneNumberValidator phoneNumberValidator;

    public void registerNewCustomer(CustomerRegistrationRequest request) {
        String phoneNumber = request.getCustomer().getPhoneNumber();

        if(!phoneNumberValidator.test(phoneNumber)) {
            throw new IllegalStateException(String.format("phone number: [%s] is not valid", phoneNumber));
        }

        Optional<Customer> customerOptional = customerRepository.selectCustomerByPhoneNumber(phoneNumber);
        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            if (customer.getName().equals(request.getCustomer().getName())) {
                return;
            }
                throw new IllegalStateException(String.format("phone number: [%s] is taken", phoneNumber));
            }

            if (request.getCustomer().getId() == null) {
                request.getCustomer().setId(randomUUID());
            }

            customerRepository.save(request.getCustomer());
        }
    }
