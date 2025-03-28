package com.bci.usersapp.service;


import com.bci.usersapp.dto.response.UserResponse;
import com.bci.usersapp.exception.UserException;
import com.bci.usersapp.repository.UserRepository;
import com.bci.usersapp.utils.JwtUtil;
import com.bci.usersapp.utils.TestUtils;
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

    @Test
    @DisplayName("create user is ok")
    void whenSignUserOk() {
        ReflectionTestUtils.setField(authService, "regexpPassword", "^(?=.*[A-Z])(?=.*\\d.*\\d)(?=.*[a-z])[a-zA-Z\\d]{8,12}$");
        ReflectionTestUtils.setField(authService, "regexpEmail", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$");

        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(this.passwordEncoder.encode(any())).thenReturn(TestUtils.PASSWORD);
        when(this.jwtUtil.generateToken(any())).thenReturn(TestUtils.TOKEN);
        when(this.userRepository.save(any())).thenReturn(TestUtils.userFound());

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
        ReflectionTestUtils.setField(authService, "regexpPassword", "^(?=.*[A-Z])(?=.*\\d.*\\d)(?=.*[a-z])[a-zA-Z\\d]{8,12}$");
        ReflectionTestUtils.setField(authService, "regexpEmail", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$");

        var exception = assertThrows(UserException.class, () -> {
            this.authService.signup(TestUtils.userEmailIncorrect());
        });

        assertEquals(HttpStatus.BAD_REQUEST.value(), exception.getErrorResponse().getCodigo());
        assertTrue(exception.getMessage().contains("Correo formato incorrecto"));

        verifyNoInteractions(this.userRepository);
        verifyNoInteractions(this.passwordEncoder);
        verifyNoInteractions(this.jwtUtil);
        verifyNoInteractions(this.userRepository);
    }

    @Test
    @DisplayName("when the email already exist")
    void whenSignUserEmailExist() {
        ReflectionTestUtils.setField(authService, "regexpPassword", "^(?=.*[A-Z])(?=.*\\d.*\\d)(?=.*[a-z])[a-zA-Z\\d]{8,12}$");
        ReflectionTestUtils.setField(authService, "regexpEmail", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$");

        when(this.userRepository.findByEmail(anyString())).thenReturn(Optional.of(TestUtils.userFound()));

        var request = TestUtils.userRequest();
        var exception = assertThrows(UserException.class, () -> {
            this.authService.signup(request);
        });

        assertEquals(HttpStatus.CONFLICT.value(), exception.getErrorResponse().getCodigo());
        assertEquals("Correo ya registrado", exception.getMessage());

        verify(this.userRepository, times(1)).findByEmail(any());
        verifyNoInteractions(this.passwordEncoder);
        verifyNoInteractions(this.jwtUtil);
        verify(this.userRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("when some error to save")
    void whenSignUserError() {
        ReflectionTestUtils.setField(authService, "regexpPassword", "^(?=.*[A-Z])(?=.*\\d.*\\d)(?=.*[a-z])[a-zA-Z\\d]{8,12}$");
        ReflectionTestUtils.setField(authService, "regexpEmail", "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z]{2,6}$");
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

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getErrorResponse().getCodigo());
        assertEquals("Problema interno.", exception.getErrorResponse().getDetail());
        assertEquals(errorMessage, exception.getMessage());

        verify(this.userRepository, times(1)).findByEmail(any());
        verify(this.passwordEncoder, times(1)).encode(any());
        verify(this.jwtUtil, times(1)).generateToken(any());
        verify(this.userRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("when token is ok and found user")
    void whenProcessLoginIsSuccessful() {
        UserDetails userDetails = TestUtils.userFound();
        when(jwtUtil.extractUsername(TestUtils.TOKEN)).thenReturn(TestUtils.EMAIL);
        when(userDetailsService.loadUserByUsername(TestUtils.EMAIL)).thenReturn(userDetails);
        when(jwtUtil.validateToken(TestUtils.TOKEN, userDetails)).thenReturn(true);
        when(userRepository.findByEmail(TestUtils.EMAIL)).thenReturn(Optional.of(TestUtils.userFound()));
        when(jwtUtil.generateToken(userDetails)).thenReturn("new-valid-token");

        UserResponse response = authService.processLogin(TestUtils.TOKEN);

        assertEquals(TestUtils.UUID_TEST, response.getUserId());
        assertEquals(TestUtils.EMAIL, response.getEmail());
        assertEquals("new-valid-token", response.getToken());
        assertTrue(response.getIsActive());

        verify(jwtUtil, times(1)).extractUsername(TestUtils.TOKEN);
        verify(userDetailsService, times(1)).loadUserByUsername(TestUtils.EMAIL);
        verify(userRepository, times(1)).findByEmail(TestUtils.EMAIL);
    }

    @Test
    @DisplayName("when token is invalid")
    void whenProcessLoginFailsDueToInvalidToken() {
        when(jwtUtil.extractUsername(TestUtils.INVALID_TOKEN)).thenReturn(null);

        UserException exception = assertThrows(UserException.class, () -> {
            authService.processLogin(TestUtils.INVALID_TOKEN);
        });

        assertEquals(HttpStatus.UNAUTHORIZED.value(), exception.getErrorResponse().getCodigo());
        assertEquals("Token inválido", exception.getMessage());

        verify(jwtUtil, times(1)).extractUsername(TestUtils.INVALID_TOKEN);
        verify(userRepository, never()).findByEmail(any());
        verify(jwtUtil, never()).generateToken(any());
    }

    @Test
    @DisplayName("when the user doesn't exist")
    void whenProcessLoginFailsDueToUserNotFound() {
        UserDetails userDetails = TestUtils.userFound();
        when(jwtUtil.extractUsername(TestUtils.TOKEN)).thenReturn(TestUtils.EMAIL);
        when(jwtUtil.validateToken(TestUtils.TOKEN, userDetails)).thenReturn(true);
        when(userDetailsService.loadUserByUsername(TestUtils.EMAIL)).thenReturn(userDetails);
        when(userRepository.findByEmail(TestUtils.EMAIL)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> {
            authService.processLogin(TestUtils.TOKEN);
        });

        assertEquals(HttpStatus.NOT_FOUND.value(), exception.getErrorResponse().getCodigo());
        assertEquals("Usuario no encontrado", exception.getMessage());

        verify(jwtUtil, times(1)).extractUsername(TestUtils.TOKEN);
        verify(userDetailsService, times(1)).loadUserByUsername(TestUtils.EMAIL);
        verify(userRepository, times(1)).findByEmail(TestUtils.EMAIL);
        verify(jwtUtil, never()).generateToken(any());
    }

}
