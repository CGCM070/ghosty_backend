package org.ghosty.service;

import lombok.RequiredArgsConstructor;
import org.ghosty.exception.ConflictException;
import org.ghosty.exception.ResourceNotFoundException;
import org.ghosty.security.CustomUserDetailsService;
import org.ghosty.security.JwtService;
import org.ghosty.model.Rol;
import org.ghosty.model.User;
import org.ghosty.dto.response.AuthResponseDTO;
import org.ghosty.dto.request.LoginRequestDTO;
import org.ghosty.dto.request.RegisterRequestDTO;
import org.ghosty.enums.Erol;
import org.ghosty.repository.RoleRepository;
import org.ghosty.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
            throw  new ConflictException("El email ya está registrado");
        }

        Rol defaultRol = rolRepository.findByRol(Erol.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Rol por defecto no encontrado"));

        User newUser = User.builder()
                .username(registerRequestDTO.username())
                .email(registerRequestDTO.email())
                .password(passwordEncoder.encode(registerRequestDTO.password()))
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
            throw new BadCredentialsException("Credenciales inválidas");
        }

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String jwtToken = jwtService.generateToken(userDetails);

        return new AuthResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                jwtToken
        );
    }

    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        System.out.println(passwordEncoder.encode("password"));
    }
}
