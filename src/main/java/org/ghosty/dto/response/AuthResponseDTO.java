package org.ghosty.dto.response;

import lombok.Builder;

@Builder
public record AuthResponseDTO(Long id, String username, String email, String token) {
}
