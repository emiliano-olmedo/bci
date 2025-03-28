package com.bci.usersapp.controller;

import com.bci.usersapp.dto.request.UserRequest;
import com.bci.usersapp.dto.response.UserResponse;
import com.bci.usersapp.exception.UserException;
import com.bci.usersapp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody UserRequest registerUserDto) {
        UserResponse userResponse = authService.signup(registerUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @GetMapping("/login")
    public ResponseEntity<UserResponse> login(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UserException(HttpStatus.BAD_REQUEST, "Debe ingresar un token con el siguiente formato: Bearer 'token'");
        }

        String token = authorizationHeader.substring(7);
        UserResponse userResponse = authService.processLogin(token);

        return ResponseEntity.ok(userResponse);
    }

}
