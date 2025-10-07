package org.ghosty.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDTO(@NotBlank  String email,
                              @NotBlank String password) {
}
