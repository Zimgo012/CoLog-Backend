package com.zimgo.colog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class CoLogApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoLogApplication.class, args);
    }

}
