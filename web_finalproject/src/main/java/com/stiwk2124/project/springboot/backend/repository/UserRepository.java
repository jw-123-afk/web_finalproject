package com.stiwk2124.project.springboot.backend.repository;

import com.stiwk2124.project.springboot.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
