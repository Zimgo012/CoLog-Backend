package com.zimgo.colog.auth.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

    @Component
    public class JWTAuthenticationFilter extends OncePerRequestFilter {


        //1. Inject dependencies
        private final JWTService jwtService;

        private final UserDetailsService userDetailsService;

        private JWTAuthenticationFilter(JWTService jwtService, UserDetailsService userDetailsService){
            this.jwtService = jwtService;
            this.userDetailsService = userDetailsService;
        }
        //2. Override doFilterInternalFunction
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

            //2.1 Check for auth header
            String authHeader = request.getHeader("Authorization");

            // 2.2 if first time login (no token yet) proceed to sign in with spring security
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            // 2.3 Check for token
            // Bearer: Example123
            //  -> extract only Example123
            String jwt = authHeader.substring(7);

            // 2.4 Extract username from token

            try {
                String username = jwtService.extractUsername(jwt);

                // 2.5 load username to security context
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 2.6 Validate the JWT
                if (jwtService.isValidToken(jwt, userDetails)) {

                    // 2.6.1 if its valid, create authentication
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, //payload
                            null, // credentials
                            userDetails.getAuthorities()); //

                    // 2.6.2 Store in Security Context
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }

            } catch (JwtException e){response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);}

            filterChain.doFilter(request,response);

        }
}
