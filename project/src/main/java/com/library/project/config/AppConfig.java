package com.library.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean // Diz ao Spring: "Quando alguém pedir um PasswordEncoder, execute este método"
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Estamos escolhendo o algoritmo BCrypt
    }
}