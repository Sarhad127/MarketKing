package org.sarhad.marketking.security;


import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.repository.UsersRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private UsersRepository usersRepository;

    private static final List<String> VALID_ROLES = List.of("USER");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> userOptional = usersRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            return new CustomUserDetails(
                    user.getId(),
                    user.getUsername(),
                    user.getPassword(),
                    getAuthorities(user)
            );
        } else {
            throw new UsernameNotFoundException("Username not found: " + username);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Users user) {
        return Stream.of(user.getRole().split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .map(this::sanitizeRole)
                .filter(this::isValid)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    private boolean isValid(String role) {
        return VALID_ROLES.contains(role);
    }

    private String sanitizeRole(String role) {
        return role.replaceAll("[^A-Z]", "");
    }
}
