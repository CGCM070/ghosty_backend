package org.ghosty.dto;


import lombok.Builder;

@Builder
public record RegisterRequestDTO(String username, String email, String password) {
}
