package org.sarhad.marketking.controllers;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.security.CustomUserDetails;
import org.sarhad.marketking.security.PasswordEncoderConfig;
import org.sarhad.marketking.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoderConfig passwordEncoderConfig;

    @GetMapping("/info")
    public String showUserInfo(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        Long userId = userDetails.getUserId();
        Users user = userService.findById(userId).orElse(null);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found");
            return "userInfo";
        }

        user.setPassword("");
        model.addAttribute("user", user);
        return "userInfo";
    }

    @PostMapping("/update")
    public String updateUserInfo(@ModelAttribute("user") Users updatedUser,
                                 @AuthenticationPrincipal CustomUserDetails userDetails,
                                 Model model) {
        if (updatedUser.getId() == null) {
            throw new RuntimeException("User ID is null, cannot update.");
        }

        Long userId = userDetails.getUserId();
        Users currentUser = userService.findById(userId).orElseThrow(() -> new RuntimeException("Current user not found"));

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            String encodedPassword = passwordEncoderConfig.passwordEncoder().encode(updatedUser.getPassword());
            currentUser.setPassword(encodedPassword);
        }

        if (!currentUser.getEmail().equals(updatedUser.getEmail())
                && userService.emailExists(updatedUser.getEmail())) {
            model.addAttribute("errorMessage", "Email already exists");
            model.addAttribute("user", updatedUser);
            return "userInfo";
        } else {
            currentUser.setEmail(updatedUser.getEmail());
        }

        currentUser.setUsername(updatedUser.getUsername());
        currentUser.setAddress(updatedUser.getAddress());
        currentUser.setPhone(updatedUser.getPhone());

        userService.updateUser(currentUser);
        return "redirect:/user/info";
    }
}
