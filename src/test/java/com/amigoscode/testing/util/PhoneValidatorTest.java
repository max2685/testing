package com.amigoscode.testing.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneValidatorTest {

    private PhoneNumberValidator underTest;

    @BeforeEach
    void setUp() {
        underTest = new PhoneNumberValidator();
    }

    @Test
    void itShouldValidatePhoneNumber() {
        //given
        String phoneNumber = "+447000000000";

        //when
        boolean isValid = underTest.test(phoneNumber);

        //then
        assertThat(isValid).isTrue();
    }
}
