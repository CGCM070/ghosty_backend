package org.ghosty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GoogleLoginRequestDTO(@NotBlank String token) {
}
