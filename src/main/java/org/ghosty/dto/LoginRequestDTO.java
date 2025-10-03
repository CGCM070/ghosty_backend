package org.ghosty.dto;

import lombok.Builder;

@Builder
public record LoginRequestDTO(String email, String password) {
}
