package com.zimgo.colog.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class EmailOTPService {

    private final RestClient restClient;
    private final String senderEmail;

    public EmailOTPService(
            @Value("${brevo.api-key}") String apiKey,
            @Value("${brevo.sender-email}") String senderEmail
    ) {
        this.senderEmail = senderEmail;

        this.restClient = RestClient.builder()
                .baseUrl("https://api.brevo.com/v3")
                .defaultHeader("api-key", apiKey)
                .defaultHeader("accept", "application/json")
                .defaultHeader("content-type", "application/json")
                .build();
    }

    public void sendOtpEmail(String userEmail, String otpCode) {

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", "CoLog",
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of(
                                "email", userEmail
                        )
                ),
                "subject", "CoLog - Email Verification",
                "textContent",
                "Welcome to CoLog!\n\n" +
                        "Your email verification code is:\n\n" +
                        otpCode + "\n\n" +
                        "This code will expire in 5 minutes.\n\n" +
                        "If you did not create a CoLog account, " +
                        "you can ignore this email."
        );

        restClient.post()
                .uri("/smtp/email")
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}