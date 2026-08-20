package com.zimgo.colog.auth.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
@Getter
@Setter
public class JWTProperties {

    private String secret;
    private long expiration;


}
