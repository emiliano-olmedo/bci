package com.bci.usersapp.exception;

import com.bci.usersapp.dto.response.ErrorResponse;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@ToString
@Getter
public class UserException extends RuntimeException {
    private final transient ErrorResponse errorResponse;

    public UserException(Exception exception) {
        super(exception.getMessage());
        this.errorResponse = ErrorResponse.builder()
                .message("Problema interno.")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();
    }

    public UserException(HttpStatus status, String errorMessage) {
        super(errorMessage);
        this.errorResponse = ErrorResponse.builder()
                .message(errorMessage)
                .status(status)
                .build();
    }

}
