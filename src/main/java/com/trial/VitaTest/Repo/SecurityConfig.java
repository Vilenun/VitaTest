package com.trial.VitaTest.Repo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf-> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .authorizeHttpRequests((auth ->
                        auth.requestMatchers(HttpMethod.POST, "/register")
                                .permitAll()
                                .requestMatchers(HttpMethod.GET, "/test").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/check/desc").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/check/asc").hasRole("USER")
                                .requestMatchers(HttpMethod.POST, "/create").hasRole("USER")
                                .anyRequest().permitAll())
                );
        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
