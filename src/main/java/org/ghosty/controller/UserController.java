package org.ghosty.controller;

import jakarta.validation.Valid;
import org.ghosty.dto.request.CreateUserRequestDTO;
import org.ghosty.dto.request.UpdateUserRequestDTO;
import org.ghosty.dto.response.UserResponseDTO;
import org.ghosty.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<Page<UserResponseDTO>> getUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.findAllDTO(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return  ResponseEntity.ok(userService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser (@PathVariable  Long id, @Valid @RequestBody UpdateUserRequestDTO updateUserRequestDTO) {
        return ResponseEntity.ok(userService.updateUser(id,updateUserRequestDTO));
    }

    @PostMapping("")
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody CreateUserRequestDTO createUserRequestDTO) {
        return ResponseEntity.ok(userService. createUser(createUserRequestDTO));
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }



}
