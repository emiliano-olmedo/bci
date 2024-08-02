package com.bci.usersapp.utils;

import com.bci.usersapp.dto.UserDto;
import com.bci.usersapp.dto.request.UserRequest;
import com.bci.usersapp.dto.response.PhoneResponse;
import com.bci.usersapp.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

public class TestUtils {

    public static final String EMAIL_PATTEN = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$";
    public static final String EMAIL = "emiliano@gmail.com";
    public static final String TOKEN = "IFWUHEUFIWHE912";
    public static final String PASSWORD = "a2asfGfdfdf4";
    public static final Integer NUMBER = 156768798;
    public static final Integer CITY_CODE = 351;
    public static final Integer COUNTRY_CODE = 54;
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
                .name("Test User")
                .email(EMAIL)
                .password(PASSWORD)
                .build();
    }

    public static UserDto userDto() {
        return UserDto.builder()
                .id(UUID_TEST)
                .email(EMAIL)
                .name("test")
                .createdDate(CREATED_DATE)
                .updatedDate(LAST_LOGIN)
                .lastLogin(UPDATED_DATE)
                .isActive(true)
                .build();
    }
}
