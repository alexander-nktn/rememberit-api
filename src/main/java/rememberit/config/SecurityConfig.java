package rememberit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection globally
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/signup", "/auth/signin").permitAll()  // Allow public access to signup and signin
                        .requestMatchers("/graphql").permitAll()  // Allow public access to GraphQL endpoint
                        .requestMatchers("/api/**").permitAll()  // Allow public access to REST API (adjust as needed)
                        .anyRequest().authenticated()  // Other requests require authentication
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // Add JWT filter

        return http.build();
    }

    // CorsConfigurationSource bean to define the CORS settings globally
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");  // Frontend URL
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("OPTIONS");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        // Apply CORS settings to both GraphQL and REST API paths
        source.registerCorsConfiguration("/graphql", config);
        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}