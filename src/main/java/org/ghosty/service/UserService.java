package org.ghosty.service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ghosty.dto.request.UpdateUserRequestDTO;
import org.ghosty.dto.response.UserResponseDTO;
import org.ghosty.exception.ResourceNotFoundException;
import org.ghosty.model.User;
import org.ghosty.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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

}
