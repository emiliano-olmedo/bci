package com.bci.usersapp.service;

import com.bci.usersapp.repository.UserRepository;
import com.bci.usersapp.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("when found user")
    void loadUserByUsername_Success() {

        when(userRepository.findByEmail(TestUtils.EMAIL)).thenReturn(Optional.ofNullable(TestUtils.userFound()));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(TestUtils.EMAIL);

        assertNotNull(userDetails);
        assertEquals(TestUtils.EMAIL, userDetails.getUsername());
        assertEquals(TestUtils.PASSWORD_ENCODED, userDetails.getPassword());
        assertTrue(userDetails.isEnabled());

        verify(userRepository, times(1)).findByEmail(TestUtils.EMAIL);
    }

    @Test
    @DisplayName("when user doesn't exist")
    void loadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail(TestUtils.EMAIL)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername(TestUtils.EMAIL)
        );

        assertEquals("User not found with username: " + TestUtils.EMAIL, exception.getMessage());
        verify(userRepository, times(1)).findByEmail(TestUtils.EMAIL);
    }
}