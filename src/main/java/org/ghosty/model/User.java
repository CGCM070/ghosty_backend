package org.ghosty.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank
    private String username;

    @NotBlank @Email
    private String email;

    @JsonIgnore
    @ToString.Exclude
    @NotBlank
    private String password;

    @ManyToOne ()
    @JoinColumn (name = "rol_id", nullable = false)
    private Rol rol;


}
