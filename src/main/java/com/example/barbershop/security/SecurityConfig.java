package com.example.barbershop.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import javax.sql.DataSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

@Configuration
public class SecurityConfig {

    private final CustomAuthenticationSuccessHandler successHandler;

    public SecurityConfig(CustomAuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // Disable CSRF temporarily to debug
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/static/**", "/css/**", "/js/**", "/images/**", "/login", "/access-denied").permitAll()
                .requestMatchers("/appointments/booking", "/appointments/booking/**").permitAll()
                .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "BARBER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()  // Temporarily allow all requests to debug
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/authenticateTheUser")
                .successHandler(successHandler)
                .permitAll()
            )
            .exceptionHandling(exception -> exception
                .accessDeniedPage("/access-denied")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers(
                "/css/**",
                "/js/**", 
                "/images/**",
                "/fonts/**",
                "/favicon.ico",
                "/static/**"
            );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsManager userDetailsManager(DataSource dataSource) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);
        
        userDetailsManager.setUsersByUsernameQuery(
            "select username, password, enabled from users where username=?"
        );
        userDetailsManager.setAuthoritiesByUsernameQuery(
            "select username, concat('ROLE_', role) from users where username=?"
        );
        
        return userDetailsManager;
    }
}