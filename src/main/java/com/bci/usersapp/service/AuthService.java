package com.bci.usersapp.service;

import com.bci.usersapp.dto.request.AuthRequest;
import com.bci.usersapp.dto.request.UserRequest;
import com.bci.usersapp.dto.response.UserResponse;
import com.bci.usersapp.exception.UserException;
import com.bci.usersapp.model.Phone;
import com.bci.usersapp.model.User;
import com.bci.usersapp.repository.UserRepository;
import com.bci.usersapp.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AuthService {

    @Value("${regexp.email}")
    private String regexpEmail;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String authenticate(AuthRequest authRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getEmail());
        return jwtUtil.generateToken(userDetails);
    }

    public UserResponse signup(UserRequest request) {
        try {
            if (!this.emailPattern().matcher(request.getEmail()).matches()) {
                throw new UserException(HttpStatus.BAD_REQUEST,
                        "Correo formato incorrecto: ".concat(request.getEmail()));
            }

            Optional<User> userOptional = this.userRepository.findByEmail(request.getEmail());
            if (userOptional.isPresent()) {
                throw new UserException(HttpStatus.CONFLICT, "Correo ya registrado");
            }

            final var uuid = UUID.randomUUID();
            final var password = this.passwordEncoder.encode(request.getPassword());
            final var user = User.builder()
                    .id(uuid)
                    .name(request.getName())
                    .email(request.getEmail())
                    .password(password)
                    .isActive(false)
                    .build();

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

            final var token = this.jwtUtil.generateToken(user);
            user.setToken(token);

            var userSaved = this.userRepository.save(user);
            return UserResponse.builder()
                    .userId(userSaved.getId())
                    .created(userSaved.getCreatedDate())
                    .modified(userSaved.getUpdatedDate())
                    .lastLogin(userSaved.getLastLogin())
                    .token(userSaved.getToken())
                    .isActive(userSaved.getIsActive())
                    .build();
        } catch (DataAccessException ex) {
            throw new UserException(ex);
        }
    }

    private Pattern emailPattern() {
        return Pattern.compile(this.regexpEmail, Pattern.CASE_INSENSITIVE);
    }
}