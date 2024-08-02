package com.bci.usersapp.service;

import com.bci.usersapp.dto.UserDto;
import com.bci.usersapp.exception.UserException;
import com.bci.usersapp.model.User;
import com.bci.usersapp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private JwtUtil jwtUtil;

    public UserDto getUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails)) {
            throw new UserException(HttpStatus.UNAUTHORIZED, "se requiere un token valido");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();


        String newToken = jwtUtil.generateToken(userDetails);

        if (userDetails instanceof User) {
            User user = (User) userDetails;
            return UserDto.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .name(user.getName())
                    .createdDate(user.getCreatedDate())
                    .updatedDate(user.getUpdatedDate())
                    .lastLogin(user.getLastLogin())
                    .isActive(user.getIsActive())
                    .build();
        }

        throw new UserException(HttpStatus.UNAUTHORIZED, "se requiere un token valido");
    }
}
