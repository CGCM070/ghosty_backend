package org.ghosty.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ghosty.dto.request.CreateUserRequestDTO;
import org.ghosty.dto.request.UpdateUserRequestDTO;
import org.ghosty.dto.response.UserResponseDTO;
import org.ghosty.enums.Erol;
import org.ghosty.exception.BadRequestException;
import org.ghosty.exception.ConflictException;
import org.ghosty.exception.ResourceNotFoundException;
import org.ghosty.model.Rol;
import org.ghosty.model.User;
import org.ghosty.repository.RoleRepository;
import org.ghosty.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponseDTO> findAllDTO(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(user -> UserResponseDTO.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .rol(user.getRol().getRol())
                        .build()
                );
    }


    public UserResponseDTO findById (Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado")
        );
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .rol(user.getRol().getRol())
                .build();
    }

    @Transactional
    public UserResponseDTO updateUser (Long id, @Valid UpdateUserRequestDTO updateUserRequestDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Usuario con id " + id + " no encontrado")
        );
        user.setUsername(updateUserRequestDTO.username());
        user.setEmail(updateUserRequestDTO.email());
        User updated = userRepository.save(user);
        return UserResponseDTO.builder()
                .id(updated.getId())
                .username(updated.getUsername())
                .email(updated.getEmail())
                .rol(updated.getRol().getRol())
                .build();
    }


    @Transactional
    public UserResponseDTO createUser(@Valid CreateUserRequestDTO createUserRequestDTO) {

        if (userRepository.findByEmail(createUserRequestDTO.email()).isPresent()) {
            throw new ConflictException("El email ya está registrado");
        }

        // string del rol a enum Erol
        Erol rolEnum;
        try {

            String roleName = createUserRequestDTO.rol().toUpperCase();
            if (!roleName.startsWith("ROLE_")) {
                roleName = "ROLE_" + roleName;
            }
            rolEnum = Erol.valueOf(roleName);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Rol inválido. Los roles permitidos son: ADMIN o USER");
        }


        Rol rol = roleRepository.findByRol(rolEnum)
                .orElseThrow(() -> new ResourceNotFoundException("Rol " + rolEnum + " no encontrado en la base de datos"));


        User user = User.builder()
                .username(createUserRequestDTO.username())
                .email(createUserRequestDTO.email())
                .password(passwordEncoder.encode(createUserRequestDTO.password()))
                .rol(rol)
                .build();

        User savedUser = userRepository.save(user);


        return UserResponseDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .rol(savedUser.getRol().getRol())
                .build();
    }


    @Transactional
    public void  deleteUser (Long id) {
        userRepository.deleteById(id);
    }
}
