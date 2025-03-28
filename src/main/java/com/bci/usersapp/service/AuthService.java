package com.bci.usersapp.service;

import com.bci.usersapp.dto.request.UserRequest;
import com.bci.usersapp.dto.response.PhoneResponse;
import com.bci.usersapp.dto.response.UserResponse;
import com.bci.usersapp.exception.UserException;
import com.bci.usersapp.model.Phone;
import com.bci.usersapp.model.User;
import com.bci.usersapp.repository.UserRepository;
import com.bci.usersapp.utils.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

    @Value("${regexp.email}")
    private String regexpEmail;

    @Value("${regexp.password}")
    private String regexpPassword;

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(JwtUtil jwtUtil,
                       UserDetailsService userDetailsService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse signup(UserRequest request) {
        try {
            if (!this.isValidPassword(request.getPassword())) {
                LOGGER.info("Incorrect password format: {}", request.getPassword());
                throw new UserException(HttpStatus.BAD_REQUEST, "La contraseña no cumple con los requisitos de formato");
            }

            LOGGER.info("Password format OK");

            if (!this.emailPattern().matcher(request.getEmail()).matches()) {
                LOGGER.info("Incorrect email format");
                throw new UserException(HttpStatus.BAD_REQUEST, "Correo formato incorrecto: ".concat(request.getEmail()));
            }

            LOGGER.info("Email format OK");

            Optional<User> userOptional = this.userRepository.findByEmail(request.getEmail());
            LOGGER.info("Búsqueda de usuario con email: {}", request.getEmail() );
            if (userOptional.isPresent()) {
                LOGGER.info("El usuario {} ya se encuentra registrado", request.getEmail());
                throw new UserException(HttpStatus.CONFLICT, "Correo ya registrado");
            }

            final var uuid = UUID.randomUUID();
            final var password = this.passwordEncoder.encode(request.getPassword());
            LOGGER.info("Password encoded: {}", password);
            final var user = User.builder()
                    .id(uuid)
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(password)
                    .isActive(true)
                    .build();

            if (request.getPhones() != null && !request.getPhones().isEmpty()) {
                var phones = request.getPhones()
                        .stream()
                        .map(phone -> Phone.builder()
                                .user(user)
                                .number(phone.getNumber())
                                .cityCode(phone.getCityCode())
                                .countryCode(phone.getCountryCode())
                                .build())
                        .collect(Collectors.toList());

                user.setPhoneList(phones);
            }

            final var token = this.jwtUtil.generateToken(user);
            user.setToken(token);

            var userSaved = this.userRepository.save(user);
            LOGGER.info("Se guarda nuevo usuario con id {}", userSaved.getId());
            return UserResponse.builder()
                    .userId(userSaved.getId())
                    .created(userSaved.getCreatedDate())
                    .lastLogin(userSaved.getLastLogin())
                    .token(userSaved.getToken())
                    .isActive(userSaved.getIsActive())
                    .build();
        } catch (DataAccessException ex) {
            throw new UserException(ex);
        }
    }

    public UserResponse processLogin(String token) {

        String email = jwtUtil.extractUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (email == null || !jwtUtil.validateToken(token, userDetails)) {
            throw new UserException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        LOGGER.info("Token válido");
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UserException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        String newToken = jwtUtil.generateToken(userDetails);

        LOGGER.info("Se recuperan datos para el siguiente usuario: {}", user.getId());

        return UserResponse.builder()
                .userId(user.getId())
                .created(user.getCreatedDate())
                .modified(user.getUpdatedDate())
                .lastLogin(LocalDateTime.now())
                .token(newToken)
                .isActive(user.getIsActive())
                .name(user.getName())
                .email(user.getEmail())
                .password(user.getPassword())
                .phones(Optional.ofNullable(user.getPhoneList())
                .orElse(Collections.emptyList())
                .stream()
                .map(phone -> new PhoneResponse(phone.getNumber(), phone.getCityCode(), phone.getCountryCode()))
                .collect(Collectors.toList()))
                .build();

    }

    private boolean isValidPassword(String password) {
        LOGGER.info("Validación de contraseña");
        return Pattern.matches(regexpPassword, password);
    }

    private Pattern emailPattern() {
        LOGGER.info("Validación de email");
        return Pattern.compile(this.regexpEmail, Pattern.CASE_INSENSITIVE);
    }

}