package org.ghosty.domain;

import jakarta.persistence.*;
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
    private String username;
    private String email;
    private String password;
    private Date connectedAt;

    @ManyToOne ()
    @JoinColumn (name = "rol_id", nullable = false)
    private Rol rol;


}
