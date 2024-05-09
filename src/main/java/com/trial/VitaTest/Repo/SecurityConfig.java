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
                        auth.requestMatchers(HttpMethod.POST, "/register").permitAll()
                                .requestMatchers(HttpMethod.GET, "/checkRequests").hasRole("USER")
                                .requestMatchers(HttpMethod.POST, "/create").hasRole("USER")
                                .requestMatchers(HttpMethod.PUT, "/send").hasRole("USER")
                                .requestMatchers(HttpMethod.PUT, "/update").hasRole("USER")
                                .requestMatchers(HttpMethod.GET, "/checkSent").hasRole("OPER")
                                .requestMatchers(HttpMethod.GET, "/checkName").hasRole("OPER")
                                .requestMatchers(HttpMethod.PUT, "/decision").hasRole("OPER")
                                .requestMatchers(HttpMethod.GET, "/users").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PUT, "/giveOperatorRole").hasRole("ADMIN")
                                .anyRequest().denyAll())
                );
        return http.build();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
