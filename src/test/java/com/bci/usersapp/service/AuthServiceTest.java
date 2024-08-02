package com.bci.usersapp.service;


import com.bci.usersapp.dto.request.AuthRequest;
import com.bci.usersapp.dto.request.UserRequest;
import com.bci.usersapp.dto.response.UserResponse;
import com.bci.usersapp.exception.UserException;
import com.bci.usersapp.model.User;
import com.bci.usersapp.repository.UserRepository;
import com.bci.usersapp.utils.JwtUtil;
import com.bci.usersapp.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(this.authService, "regexpEmail", TestUtils.EMAIL_PATTEN);
    }

    @Test
    @DisplayName("create user is ok")
    void whenSignUserOk() {
        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(any())).thenReturn(TestUtils.PASSWORD);
        when(this.jwtUtil.generateToken(any())).thenReturn(TestUtils.TOKEN);
        when(this.userRepository.save(any())).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(TestUtils.UUID_TEST);
            return user;
        });

        var response = this.authService.signup(TestUtils.userRequest());
        assertEquals(TestUtils.UUID_TEST, response.getUserId());

        verify(this.userRepository, times(1)).findByEmail(any());
        verify(this.passwordEncoder, times(1)).encode(any());
        verify(this.jwtUtil, times(1)).generateToken(any());
        verify(this.userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("when the email in request is invalid format")
    void whenSignUserInvalidEmail() {
        var requestInvalidEmail = UserRequest.builder()
                .email("email@gmail")
                .build();

        var exception = assertThrows(UserException.class, () -> {
            this.authService.signup(requestInvalidEmail);
        });

        assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorResponse().getStatus());
        assertTrue(exception.getMessage().contains("Correo formato incorrecto"));

        verifyNoInteractions(this.userRepository);
        verifyNoInteractions(this.passwordEncoder);
        verifyNoInteractions(this.jwtUtil);
        verifyNoInteractions(this.userRepository);
    }

    @Test
    @DisplayName("when the email already exist")
    void whenSignUserEmailExist() {
        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtils.userFound()));

        var request = TestUtils.userRequest();
        var exception = assertThrows(UserException.class, () -> {
            this.authService.signup(request);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getErrorResponse().getStatus());
        assertEquals("Correo ya registrado", exception.getMessage());

        verify(this.userRepository, times(1)).findByEmail(any());
        verifyNoInteractions(this.passwordEncoder);
        verifyNoInteractions(this.jwtUtil);
        verify(this.userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("when some error to save")
    void whenSignUserError() {
        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(any())).thenReturn(TestUtils.PASSWORD);
        when(this.jwtUtil.generateToken(any())).thenReturn(TestUtils.TOKEN);

        var errorMessage = "integrity violation";
        var dataIntegrityViolationException = new DataIntegrityViolationException(errorMessage);
        when(this.userRepository.save(any())).thenThrow(dataIntegrityViolationException);

        var request = TestUtils.userRequest();
        var exception = assertThrows(UserException.class, () -> {
            this.authService.signup(request);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getErrorResponse().getStatus());
        assertEquals("Problema interno.", exception.getErrorResponse().getMessage());
        assertEquals(errorMessage, exception.getMessage());

        verify(this.userRepository, times(1)).findByEmail(any());
        verify(this.passwordEncoder, times(1)).encode(any());
        verify(this.jwtUtil, times(1)).generateToken(any());
        verify(this.userRepository, times(1)).save(any());
    }
}
