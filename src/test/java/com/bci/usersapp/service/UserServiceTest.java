package com.bci.usersapp.service;

import com.bci.usersapp.dto.UserDto;
import com.bci.usersapp.exception.UserException;
import com.bci.usersapp.model.User;
import com.bci.usersapp.utils.JwtUtil;
import com.bci.usersapp.utils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Get user details successfully")
    void getUserDetailsSuccess() {
        User user = TestUtils.userFound();

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(user);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn(TestUtils.TOKEN);

        UserDto userDto = userService.getUserDetails();

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getCreatedDate(), userDto.getCreatedDate());
        assertEquals(user.getUpdatedDate(), userDto.getUpdatedDate());
        assertEquals(user.getLastLogin(), userDto.getLastLogin());
        assertEquals(user.getIsActive(), userDto.getIsActive());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(2)).getPrincipal();
        verify(jwtUtil, times(1)).generateToken(any(UserDetails.class));
    }

    @Test
    @DisplayName("Throw exception when user is not authenticated")
    void getUserDetailsUnauthorized() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(new Object()); // Simula un principal no válido

        UserException exception = assertThrows(UserException.class, () -> userService.getUserDetails());

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getErrorResponse().getStatus());
        assertEquals("se requiere un token valido", exception.getMessage());

        verify(securityContext, times(1)).getAuthentication();
        verify(authentication, times(1)).getPrincipal();
        verify(jwtUtil, never()).generateToken(any(UserDetails.class));
    }
}
