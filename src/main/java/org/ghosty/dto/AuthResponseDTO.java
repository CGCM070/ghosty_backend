package org.ghosty.dto;

import lombok.Builder;

@Builder
public record AuthResponseDTO(Long id, String username, String email, String token) {
}
