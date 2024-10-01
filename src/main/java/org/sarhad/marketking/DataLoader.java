package org.sarhad.marketking;

import lombok.AllArgsConstructor;
import org.sarhad.marketking.model.Users;
import org.sarhad.marketking.repository.UsersRepository;
import org.sarhad.marketking.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Configuration
@AllArgsConstructor
public class DataLoader implements CommandLineRunner {

    private ProductService productService;
    private UsersRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        productService.fetchAndStoreProducts();
    }

    @Bean
    public CommandLineRunner initializeData(PasswordEncoder passwordEncoder) {
        return args -> {
            String password = passwordEncoder.encode("asdasd123");
            Users user1 = new Users("Sarhad", password, "Sarhad@hotmail.com", "Laxbacken 11", "0704730256", "USER");
            Users user2 = new Users("sarhad.bahrami@yh.nackademin.se", password, "Sarhad.Bahrami@yh.hotmail.com", "Laxbacken 11", "0704730256", "USER");
            userRepository.save(user1);
            userRepository.save(user2);
        };
    }
}
