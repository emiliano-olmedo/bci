package com.bci.usersapp.utils;

import com.bci.usersapp.dto.request.UserRequest;
import com.bci.usersapp.dto.response.PhoneResponse;
import com.bci.usersapp.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

public class TestUtils {

    public static final String EMAIL_PATTERN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$";
    public static final String PASSWORD_PATTERN = "^(?=(.*[A-Z]){1})(?=(.*\\\\d){2})(?=(.*[a-z]){1})(?!.*[^a-zA-Z0-9]).{8,12}$";
    public static final String EMAIL = "emiliano@gmail.com";
    public static final String TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJlbWlsaWFub0BiY2kuY29tIiwiZXhwIjoxNzQyODgzNjQ2LCJpYXQiOjE3NDI4NDc2NDZ9.dnaJOhFxUjfdguObGXmo88yKzWK-4jcQE0lRXNZa-lM";
    public static final String INVALID_TOKEN = "ASD";
    public static final String PASSWORD = "a2asfGfdfdf4";
    public static final String PASSWORD_ENCODED = "$2a$10$0LHzQR.7sLmUANMMDkL6debd98JcJbeso392YqT7DYLoVry3MT1ju";
    public static final Long NUMBER = 156768798L;
    public static final Integer CITY_CODE = 351;
    public static final String COUNTRY_CODE = "54";
    public static final UUID UUID_TEST = UUID.randomUUID();
    public static final LocalDateTime CREATED_DATE = LocalDateTime.now();
    public static final LocalDateTime UPDATED_DATE = LocalDateTime.now();
    public static final LocalDateTime LAST_LOGIN = LocalDateTime.now();

    public static UserRequest userRequest() {
        return UserRequest.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .phones(Collections.singletonList(PhoneResponse.builder()
                        .number(NUMBER)
                        .cityCode(CITY_CODE)
                        .countryCode(COUNTRY_CODE)
                        .build()))
                .build();
    }

    public static User userFound() {
        return User.builder()
                .id(UUID_TEST)
                .createdDate(CREATED_DATE)
                .lastLogin(LAST_LOGIN)
                .name("Test User")
                .email(EMAIL)
                .password(PASSWORD_ENCODED)
                .isActive(true)
                .build();
    }

    public static UserRequest userEmailIncorrect() {
        return UserRequest.builder()
                .email("emiliano@gmail")
                .password(PASSWORD)
                .build();
    }
}
