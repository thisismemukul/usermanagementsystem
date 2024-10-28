package com.user.management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests.anyRequest().authenticated());
/*       .requestMatchers("/public/**").permitAll()
          .requestMatchers("/admin").denyAll()
           http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
 */
        http.csrf(AbstractHttpConfigurer::disable);
        //http.formLogin(withDefaults());
        http.httpBasic(withDefaults());
        return http.build();
    }
}
