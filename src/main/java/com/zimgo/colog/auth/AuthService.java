package com.zimgo.colog.auth;

import com.zimgo.colog.auth.dto.LoginRequest;
import com.zimgo.colog.auth.dto.LoginResponse;
import com.zimgo.colog.auth.dto.RegisterRequest;
import com.zimgo.colog.auth.dto.RegisterResponse;
import com.zimgo.colog.auth.security.JWTService;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRole;
import com.zimgo.colog.user.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;


    public AuthService(UserService userService,
                       AuthenticationManager authenticationManager,
                       JWTService jwtService,
                        PasswordEncoder passwordEncoder){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest req){

        //Authenticate new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        //Generate JWT Token
        return new LoginResponse(token);

    }

    public RegisterResponse register(RegisterRequest req){
        User user = new User();
        user.setFirstName(req.getFirstName());
        user.setLastName(req.getLastName());
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setRole(UserRole.USER);
        user.setPassword(passwordEncoder.encode(req.getPassword()));

        //Save new user
        userService.addUser(user);

        RegisterResponse resp = new RegisterResponse(user.getFirstName(), user.getLastName(), user.getEmail());
        return resp;
    }
}

