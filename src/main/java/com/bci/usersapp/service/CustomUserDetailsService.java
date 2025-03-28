package com.bci.usersapp.service;

import com.bci.usersapp.model.User;
import com.bci.usersapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LOGGER.info("Buscando usuario con username: {}", username);
        User user = userRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException("User not found with username: " + username));
        LOGGER.info("Usuario {} encontrado", username);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                user.getIsActive(), true, true, true, Collections.emptyList()
        );
    }
}