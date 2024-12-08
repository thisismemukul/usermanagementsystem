package com.user.management.security;

import com.user.management.enums.AppRole;
import com.user.management.models.Role;
import com.user.management.models.User;
import com.user.management.repositories.RoleRepository;
import com.user.management.repositories.UserRepository;
import com.user.management.security.jwt.AuthEntryPointJwt;
import com.user.management.security.jwt.AuthTokenFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.time.LocalDate;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * SecurityConfig is the main configuration class for Spring Security.
 * It defines the security filter chain, authentication manager, password encoder,
 * and other related configurations for the application.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(
        prePostEnabled = true,
        securedEnabled = true,
        jsr250Enabled = true
)
public class SecurityConfig {

    private final AuthEntryPointJwt unauthorizedHandler;

    /**
     * Constructor injection for AuthEntryPointJwt to handle unauthorized access attempts.
     *
     * @param unauthorizedHandler the entry point that handles authentication errors
     */
    public SecurityConfig(AuthEntryPointJwt unauthorizedHandler) {
        this.unauthorizedHandler = unauthorizedHandler;
    }

    /**
     * Defines the JWT token filter that processes incoming requests and validates JWT tokens.
     *
     * @return an instance of AuthTokenFilter
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Configures the security filter chain for the application.
     * Sets up CORS, CSRF protection, authentication mechanisms, and request authorizations.
     *
     * @param http HttpSecurity object to configure security settings
     * @return a configured SecurityFilterChain instance
     * @throws Exception if there is an error in the security configuration
     */
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.cors(withDefaults()) // Enable Cross-Origin Resource Sharing
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // Use cookies for CSRF tokens
                        .ignoringRequestMatchers("/api/auth/public/**")) // Exclude public auth endpoints from CSRF protection
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Admin endpoints require ADMIN role
                        .requestMatchers("/api/csrf-token").permitAll() // CSRF token endpoint is accessible to all
                        .requestMatchers("/api/auth/public/**").permitAll() // Public auth endpoints are accessible to all
                        .requestMatchers("/oauth2/**").permitAll() // OAuth2 endpoints are accessible to all
                        .anyRequest().authenticated()) // All other requests require authentication
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)) // Handle unauthorized access attempts
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class) // Add JWT token filter
                .addFilterBefore(new CustomLoggingFilter(), UsernamePasswordAuthenticationFilter.class) // Add custom logging filter
                .addFilterAfter(new RequestValidationFilter(), CustomLoggingFilter.class) // Add request validation filter
                .formLogin(withDefaults()) // Enable default form-based login
                .httpBasic(withDefaults()); // Enable default HTTP Basic authentication

        return http.build();
    }

    /**
     * Provides an AuthenticationManager instance for managing authentication.
     *
     * @param authenticationConfiguration configuration for the authentication manager
     * @return an AuthenticationManager instance
     * @throws Exception if the authentication manager cannot be created
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Provides a PasswordEncoder instance for encoding and decoding passwords.
     * Uses BCrypt hashing algorithm for secure password storage.
     *
     * @return a PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CommandLineRunner to initialize default roles and users in the database.
     * Creates default roles (USER, ADMIN) and adds sample users if they do not already exist.
     *
     * @param roleRepository the RoleRepository for managing roles
     * @param userRepository the UserRepository for managing users
     * @param passwordEncoder the PasswordEncoder for encoding user passwords
     * @return a CommandLineRunner instance
     */
    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_USER))); // Create USER role if not found

            Role adminRole = roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                    .orElseGet(() -> roleRepository.save(new Role(AppRole.ROLE_ADMIN))); // Create ADMIN role if not found

            if (!userRepository.existsByUsername("user1")) { // Create default user if not exists
                User user1 = new User("user1", "user1@example.com",
                        passwordEncoder.encode("password1"));
                user1.setAccountNonLocked(false);
                user1.setAccountNonExpired(true);
                user1.setCredentialsNonExpired(true);
                user1.setEnabled(true);
                user1.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                user1.setAccountExpiryDate(LocalDate.now().plusYears(1));
                user1.setTwoFactorEnabled(false);
                user1.setSignUpMethod("email");
                user1.setRole(userRole);
                userRepository.save(user1);
            }

            if (!userRepository.existsByUsername("admin")) { // Create default admin if not exists
                User admin = new User("admin", "admin@example.com",
                        passwordEncoder.encode("adminPass"));
                admin.setAccountNonLocked(true);
                admin.setAccountNonExpired(true);
                admin.setCredentialsNonExpired(true);
                admin.setEnabled(true);
                admin.setCredentialsExpiryDate(LocalDate.now().plusYears(1));
                admin.setAccountExpiryDate(LocalDate.now().plusYears(1));
                admin.setTwoFactorEnabled(false);
                admin.setSignUpMethod("email");
                admin.setRole(adminRole);
                userRepository.save(admin);
            }
        };
    }
}
