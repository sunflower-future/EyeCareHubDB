package com.example.EyeCareHubDB.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.EyeCareHubDB.filter.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final JwtAuthenticationFilter jwtAuthFilter;
        private final AuthenticationProvider authenticationProvider;
        private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .exceptionHandling(exception -> exception
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint))
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/api/auth/**",
                                                                "/v3/api-docs",
                                                                "/v3/api-docs/**",
                                                                "/swagger-ui/**",
                                                                "/swagger-ui.html",
                                                                "/swagger-resources",
                                                                "/swagger-resources/**",
                                                                "/configuration/ui",
                                                                "/configuration/security",
                                                                "/webjars/**",
                                                                "/api-docs/**",
                                                                "/api-docs")
                                                .permitAll()
                                                .requestMatchers(org.springframework.http.HttpMethod.GET,
                                                                "/api/v1/products/**", "/api/v1/categories/**",
                                                                "/api/v1/policies/**", "/api/v1/variants/**",
                                                                "/api/v1/media/**")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }
}
