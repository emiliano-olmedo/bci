package com.bci.usersapp.exception;

import com.bci.usersapp.dto.response.ErrorResponse;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@ToString
@Getter
public class UserException extends RuntimeException {
    private final transient ErrorResponse errorResponse;

    public UserException(Exception exception) {
        super(exception.getMessage());
        this.errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .codigo(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .detail("Problema interno.")
                .build();
    }

    public UserException(HttpStatus status, String errorMessage) {
        super(errorMessage);
        this.errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .codigo(status.value())
                .detail(errorMessage)
                .build();
    }
}