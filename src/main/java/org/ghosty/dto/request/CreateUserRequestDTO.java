package org.ghosty.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record CreateUserRequestDTO (@NotBlank  String username,
                                    @NotBlank @Email String email,
                                    @NotBlank String password,
                                    @NotBlank String rol ){
}
