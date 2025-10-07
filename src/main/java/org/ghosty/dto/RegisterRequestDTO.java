package org.ghosty.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record RegisterRequestDTO(@NotBlank String username,
                                 @NotBlank String email,
                                 @NotBlank String password) {
}
