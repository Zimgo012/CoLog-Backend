package com.zimgo.colog.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

    @Service
    public class JWTService {


        private final JWTProperties jwtProperties;
        private final SecretKey secretKey;

        public JWTService(JWTProperties jwtProperties){
            this.jwtProperties = jwtProperties;
            this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.getSecret()));
        }

        public String generateToken(UserDetails userDetails){

            String jwt =  Jwts.builder()
                    .subject(userDetails.getUsername())
                    .issuedAt(new Date())
                    .expiration(
                            new Date(System.currentTimeMillis() + jwtProperties.getExpiration())
                    )
                    .signWith(secretKey)
                    .compact();

            return jwt;
        }



        public boolean isValidToken(String token, UserDetails userDetails){
            String username = extractUsername(token);

            boolean isSameToken = username.equals(userDetails.getUsername());

            return isSameToken && !isTokenExpired(token);
        };

        public String extractUsername(String token){
            String username = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();

            return username;
        }

        private boolean isTokenExpired(String token){
            Date expiration = Jwts.parser()
                    .verifyWith(secretKey) //Verify if same secretKey
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();

            return expiration.before(new Date()); // Token < at check
        }

    }
