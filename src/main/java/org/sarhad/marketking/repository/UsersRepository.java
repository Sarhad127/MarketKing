package org.sarhad.marketking.repository;

import org.sarhad.marketking.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUsername(String username);
    Optional<Users> findById(Long id);
    Optional<Users> findByEmail(String email);
}
