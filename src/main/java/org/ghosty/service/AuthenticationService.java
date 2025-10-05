package org.ghosty.service;

import lombok.RequiredArgsConstructor;
import org.ghosty.security.CustomUserDetailsService;
import org.ghosty.security.JwtService;
import org.ghosty.model.Rol;
import org.ghosty.model.User;
import org.ghosty.dto.AuthResponseDTO;
import org.ghosty.dto.LoginRequestDTO;
import org.ghosty.dto.RegisterRequestDTO;
import org.ghosty.enums.Erol;
import org.ghosty.repository.RoleRepository;
import org.ghosty.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@RequiredArgsConstructor

public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final RoleRepository rolRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;



    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {

        if (userRepository.findByEmail(registerRequestDTO.email()).isPresent()) {
            throw  new RuntimeException("Email already exists");
        }

        Rol defaultRol = rolRepository.findByRol(Erol.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));

        User newUser = User.builder()
                .username(registerRequestDTO.username())
                .email(registerRequestDTO.email())
                .password(passwordEncoder.encode(registerRequestDTO.password()))
                .connectedAt(new Date())
                .rol(defaultRol)
                .build();

        User savedUser = userRepository.save(newUser);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(savedUser.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);


        return new AuthResponseDTO(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                jwtToken
        );
    }

    @Transactional
    public AuthResponseDTO authenticate(LoginRequestDTO request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new RuntimeException("Inválid credentials");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jwtToken
        );
    }

}
