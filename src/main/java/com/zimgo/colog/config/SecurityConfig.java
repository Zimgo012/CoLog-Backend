package com.zimgo.colog.config;

import com.zimgo.colog.auth.security.JWTAuthenticationFilter;
import com.zimgo.colog.auth.security.JWTProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableConfigurationProperties(JWTProperties.class)
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    final public JWTAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JWTAuthenticationFilter jwtAuthenticationFilter ){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    //Security Filter Chain Configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, ex) ->
                                writeSecurityError(response, HttpServletResponse.SC_UNAUTHORIZED,
                                        "AUTHENTICATION_REQUIRED", "Authentication is required"))
                        .accessDeniedHandler((request, response, ex) ->
                                writeSecurityError(response, HttpServletResponse.SC_FORBIDDEN,
                                        "ACCESS_DENIED", "You do not have permission to access this resource"))
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                //Authorization Config
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/diary/all")
                            .hasRole("USER")
                        .requestMatchers("/auth/**")
                            .permitAll()
                        .requestMatchers("/ws/**")
                            .permitAll()
                        .requestMatchers("/user/check/**")
                            .permitAll()
                        .anyRequest()
                            .authenticated()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // Vite origin
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply globally
        return source;
    }


    // Authentication Provider Configuration
    @Bean
    public AuthenticationProvider authenticationProvider (
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder){

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;

    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    private void writeSecurityError(HttpServletResponse response, int status, String code, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"status\":" + status + ",\"code\":\"" + code
                + "\",\"message\":\"" + message + "\"}");
    }

}
