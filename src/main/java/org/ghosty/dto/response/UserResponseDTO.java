package org.ghosty.dto.response;

import lombok.Builder;
import org.ghosty.enums.Erol;

@Builder
public record UserResponseDTO (Long id, String username, String email, Erol rol  ) {
}
