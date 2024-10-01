package org.sarhad.marketking.service.serviceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.repository.UsersRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    @Mock
    private UsersRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Users user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Users();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setAddress("123 Test St");
        user.setPhone("1234567890");
    }

    @Test
    void testFindByUsername_UserExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        Optional<Users> result = userService.findByUsername("testuser");

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_UserDoesNotExist() {
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        Optional<Users> result = userService.findByUsername("nonexistentuser");

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
    }

    @Test
    void testSaveUser() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.saveUser(user);

        assertEquals("encodedPassword", user.getPassword());
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_UserExists() {
        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setAddress("456 Updated St");
        updatedUser.setPhone("0987654321");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.updateUser(updatedUser);

        assertEquals("updatedUser", user.getUsername());
        assertEquals("updated@example.com", user.getEmail());
        assertEquals("456 Updated St", user.getAddress());
        assertEquals("0987654321", user.getPhone());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testUpdateUser_UserDoesNotExist() {
        Users updatedUser = new Users();
        updatedUser.setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateUser(updatedUser);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findById(2L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testEmailExists_EmailExists() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        boolean result = userService.emailExists("test@example.com");

        assertTrue(result);
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testEmailExists_EmailDoesNotExist() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        boolean result = userService.emailExists("nonexistent@example.com");

        assertFalse(result);
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    void testFindById_UserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<Users> result = userService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(user, result.get());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserDoesNotExist() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        Optional<Users> result = userService.findById(2L);

        assertFalse(result.isPresent());
        verify(userRepository, times(1)).findById(2L);
    }

    @Test
    void checkPassword_ValidPassword_ReturnsTrue() {
        String rawPassword = "mySecretPassword";
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(true);
        boolean result = userService.checkPassword(user, rawPassword);
        assertTrue(result);
        verify(passwordEncoder).matches(rawPassword, user.getPassword());
    }

    @Test
    void checkPassword_InvalidPassword_ReturnsFalse() {
        String rawPassword = "wrongPassword";
        when(passwordEncoder.matches(rawPassword, user.getPassword())).thenReturn(false);
        boolean result = userService.checkPassword(user, rawPassword);
        assertFalse(result);
        verify(passwordEncoder).matches(rawPassword, user.getPassword());
    }
}
