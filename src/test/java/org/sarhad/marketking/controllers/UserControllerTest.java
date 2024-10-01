package org.sarhad.marketking.controllers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.service.UserService;
import org.sarhad.marketking.security.PasswordEncoderConfig;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.ui.Model;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoderConfig passwordEncoderConfig;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Model model;

    @Mock
    private CustomUserDetails customUserDetails;

    private Users user;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new Users();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setUsername("testUser");
        user.setPassword("password");
    }

    @Test
    void showUserInfo_UserIsAuthenticated_ReturnsUserInfoView() {
        when(customUserDetails.getUserId()).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));

        String viewName = userController.showUserInfo(customUserDetails, model);

        assertEquals("userInfo", viewName);
        verify(model).addAttribute("user", user);
    }

    @Test
    void showUserInfo_UserIsNotAuthenticated_RedirectsToLogin() {
        when(customUserDetails.getUserId()).thenReturn(null);

        String viewName = userController.showUserInfo(null, model);

        assertEquals("redirect:/login", viewName);
    }

    @Test
    void showUserInfo_UserNotFound_ReturnsUserInfoViewWithErrorMessage() {
        when(customUserDetails.getUserId()).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.empty());

        String viewName = userController.showUserInfo(customUserDetails, model);

        assertEquals("userInfo", viewName);
        verify(model).addAttribute("errorMessage", "User not found");
    }

    @Test
    void updateUserInfo_SuccessfulUpdate_RedirectsToUserInfo() {
        when(customUserDetails.getUserId()).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(userService.emailExists(any())).thenReturn(false);
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setUsername("newUsername");
        updatedUser.setAddress("New Address");
        updatedUser.setPhone("1234567890");
        updatedUser.setPassword("newPassword");

        String viewName = userController.updateUserInfo(updatedUser, customUserDetails, model);

        assertEquals("redirect:/user/info", viewName);
        verify(userService).updateUser(argThat(userArg ->
                userArg.getId().equals(1L) &&
                        userArg.getEmail().equals("newemail@example.com") &&
                        userArg.getUsername().equals("newUsername") &&
                        userArg.getAddress().equals("New Address") &&
                        userArg.getPhone().equals("1234567890") &&
                        userArg.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void updateUserInfo_UserIdIsNull_ThrowsRuntimeException() {
        Users updatedUser = new Users();
        updatedUser.setId(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.updateUserInfo(updatedUser, customUserDetails, model);
        });

        assertEquals("User ID is null, cannot update.", exception.getMessage());
    }

    @Test
    void updateUserInfo_EmailAlreadyExists_ReturnsUserInfoViewWithErrorMessage() {
        when(customUserDetails.getUserId()).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(userService.emailExists("existing@example.com")).thenReturn(true);

        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setEmail("existing@example.com");

        String viewName = userController.updateUserInfo(updatedUser, customUserDetails, model);

        assertEquals("userInfo", viewName);
        verify(model).addAttribute("errorMessage", "Email already exists");
        verify(model).addAttribute("user", updatedUser);
    }

    @Test
    void updateUserInfo_UpdatesEmailSuccessfully() {
        when(customUserDetails.getUserId()).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(userService.emailExists(any())).thenReturn(false);
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setEmail("newemail@example.com");

        String viewName = userController.updateUserInfo(updatedUser, customUserDetails, model);

        assertEquals("redirect:/user/info", viewName);
        verify(userService).updateUser(argThat(userArg ->
                userArg.getId().equals(1L) &&
                        userArg.getEmail().equals("newemail@example.com")
        ));
    }

    @Test
    void updateUserInfo_UpdatesPasswordSuccessfully() {
        when(customUserDetails.getUserId()).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(userService.emailExists(any())).thenReturn(false);
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedPassword");

        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPassword("newPassword");

        String viewName = userController.updateUserInfo(updatedUser, customUserDetails, model);

        assertEquals("redirect:/user/info", viewName);
        verify(userService).updateUser(argThat(userArg ->
                userArg.getId().equals(1L) &&
                        userArg.getEmail().equals("newemail@example.com") &&
                        userArg.getPassword().equals("encodedPassword")
        ));
    }

    @Test
    void updateUserInfo_UserWithoutPassword_ShouldNotChangePassword() {
        when(customUserDetails.getUserId()).thenReturn(user.getId());
        when(userService.findById(user.getId())).thenReturn(Optional.of(user));
        when(userService.emailExists(any())).thenReturn(false);
        when(passwordEncoderConfig.passwordEncoder()).thenReturn(passwordEncoder);

        Users updatedUser = new Users();
        updatedUser.setId(1L);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setPassword(null);

        String viewName = userController.updateUserInfo(updatedUser, customUserDetails, model);

        assertEquals("redirect:/user/info", viewName);
        verify(userService).updateUser(argThat(userArg ->
                userArg.getId().equals(1L) &&
                        userArg.getEmail().equals("newemail@example.com") &&
                        userArg.getPassword().equals("password")
        ));
    }
}