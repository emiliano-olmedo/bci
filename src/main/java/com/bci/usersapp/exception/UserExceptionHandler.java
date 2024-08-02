package com.bci.usersapp.exception;

import com.bci.usersapp.dto.response.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class UserExceptionHandler {

    private static final String DELIMITER_COMMA = " , ";

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var fieldErrors = ex.getBindingResult().getFieldErrors();
        var objectName = Objects.toString(ex.getObjectName(), Strings.EMPTY);
        var message = Strings.EMPTY;

        if (!fieldErrors.isEmpty()) {
            message = fieldErrors.stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.joining(DELIMITER_COMMA));
        }

        var errorResponse = ErrorResponse.builder().message(message).build();
        log.error("methodArgumentNotValidException => {} in => {}", errorResponse, objectName);
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> badCredentials(BadCredentialsException ex) {
        var errorResponse = ErrorResponse.builder().message("The username or password is incorrect").build();
        log.error("badCredentials: {} , {}", ex, errorResponse);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }

    @ExceptionHandler({AccountStatusException.class, AuthenticationException.class,
            AccessDeniedException.class, SignatureException.class, ExpiredJwtException.class})
    public ResponseEntity<ErrorResponse> generalException(Exception ex) {
        var errorResponse = ErrorResponse.builder().message(ex.getMessage()).build();
        log.error("generalException: {} ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> userException(UserException ex) {
        var errorResponse = ex.getErrorResponse();
        log.error("UserException: {} , message:{}", ex, ex.getMessage());
        return ResponseEntity.status(errorResponse.getStatus()).body(errorResponse);
    }
}
