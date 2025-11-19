package com.example.hello_spring_boot.config;

import com.example.hello_spring_boot.entity.User;
import com.example.hello_spring_boot.enums.Role;
import com.example.hello_spring_boot.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {
    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "spring", value = "dev", matchIfMissing = false)
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.existsByUsername("admin")) return;
            HashSet<String> roles = new HashSet<>();
            roles.add(Role.ADMIN.name());
            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
//                    .roles(roles)
                    .build();

            userRepository.save(user);
            log.info("Admin account have been created");
        };
    }

}
