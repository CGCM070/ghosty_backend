package org.ghosty.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateUserRequestDTO (@NotBlank String username,
                                    @NotBlank String email){
}
