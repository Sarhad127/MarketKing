package org.sarhad.marketking.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.service.UserService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
@ActiveProfiles("test")
class LoginControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void login() {
        String viewName = loginController.login();
        assertEquals("login", viewName);
    }

    @Test
    void logout() {
        String viewName = loginController.logout();
        assertEquals("logout", viewName);
    }

    @Test
    void showRegistrationForm() {
        String viewName = loginController.showRegistrationForm(model);
        verify(model).addAttribute("user", new Users());
        assertEquals("register", viewName);
    }

    @Test
    void registerUser_success() {
        Users user = new Users();
        user.setUsername("testUser");
        user.setPassword("password");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        String viewName = loginController.registerUser(user, bindingResult, model);

        verify(userService).saveUser(user);
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void registerUser_withErrors() {
        Users user = new Users();
        user.setUsername("testUser");

        when(bindingResult.hasErrors()).thenReturn(true);

        String viewName = loginController.registerUser(user, bindingResult, model);

        assertEquals("register", viewName);
    }

    @Test
    void registerUser_usernameExists() {
        Users user = new Users();
        user.setUsername("testUser");

        when(bindingResult.hasErrors()).thenReturn(false);
        when(userService.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        String viewName = loginController.registerUser(user, bindingResult, model);

        verify(model).addAttribute("registrationError", "Username already exists.");
        assertEquals("register", viewName);
    }
}
