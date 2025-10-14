package org.ghosty.repository;

import org.ghosty.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    
    Optional<User> findByGoogleId(String googleId);

    Page<User> findAll(Pageable pageable);

    Optional<User> findById(Long id);
}
