package org.sarhad.marketking.service;

import org.sarhad.marketking.model.Users;

import java.util.Optional;

public interface UserService {
    Optional<Users> findByUsername(String username);
    void saveUser(Users user);
    void updateUser(Users user);
    boolean emailExists(String email);
    Optional<Users> findById(Long id);
    boolean checkPassword(Users user, String rawPassword);
}
