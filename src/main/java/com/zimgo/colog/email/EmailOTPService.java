package com.zimgo.colog.email;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailOTPService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailOTPService(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    public void sendOtpEmail(String userEmail, String otpCode) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromEmail);
        message.setTo(userEmail);
        message.setSubject("CoLog - Email Verification");

        message.setText(
                "Welcome to CoLog!\n\n" +
                        "Your email verification code is:\n\n" +
                        otpCode + "\n\n" +
                        "This code will expire in 5 minutes.\n\n" +
                        "If you did not create a CoLog account, you can ignore this email."
        );

        javaMailSender.send(message);
    }

}
