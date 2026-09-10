package com.zimgo.colog.auth;

import com.zimgo.colog.auth.dto.EmailVerifyRequest;
import com.zimgo.colog.auth.dto.LoginRequest;
import com.zimgo.colog.auth.dto.RegisterRequest;
import com.zimgo.colog.auth.pendingRegistration.PendingRegistration;
import com.zimgo.colog.auth.security.JWTService;
import com.zimgo.colog.email.EmailOTPService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final AuthService authService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JWTService jwtService, AuthService authService) {

        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req){
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req){
        return ResponseEntity.ok(authService.register(req));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@Valid @RequestBody EmailVerifyRequest req){
        return ResponseEntity.ok(authService.verify(req));
    }

}
