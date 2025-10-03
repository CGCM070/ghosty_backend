package org.ghosty.repository;

import org.ghosty.domain.Rol;
import org.ghosty.enums.Erol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Rol, Long> {
    Optional<Rol>findByRol(Erol erol);
}
