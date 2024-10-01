package org.sarhad.marketking.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private SecurityConfig securityConfig;

    @Autowired
    private PasswordEncoderConfig passwordEncoderConfig;

    @Test
    void testSecurityFilterChain() throws Exception {
        SecurityFilterChain securityFilterChain = securityConfig.securityFilterChain(mock(HttpSecurity.class));
        assertNotNull(securityFilterChain);
    }

    @Test
    void testPasswordEncoder() {
        PasswordEncoder passwordEncoder = passwordEncoderConfig.passwordEncoder();
        assertNotNull(passwordEncoder);

        String rawPassword = "testPassword";
        String encodedPassword = passwordEncoder.encode(rawPassword);

        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
    }
}
