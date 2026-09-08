package com.zimgo.colog.auth;

import com.zimgo.colog.auth.dto.*;
import com.zimgo.colog.auth.pendingRegistration.PendingRegistration;
import com.zimgo.colog.auth.pendingRegistration.PendingRegistrationRepository;
import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.auth.security.JWTService;
import com.zimgo.colog.email.EmailOTPService;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRepository;
import com.zimgo.colog.user.UserRole;
import com.zimgo.colog.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.data.repository.Repository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class AuthService {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final PendingRegistrationRepository pendingRegistrationRepository;
    private final EmailOTPService emailOTPService;

    private final UserRepository userRepository;


    public AuthService(UserService userService,
                       AuthenticationManager authenticationManager,
                       JWTService jwtService,
                       PasswordEncoder passwordEncoder,
                       PendingRegistrationRepository pendingRegistrationRepository,
                       EmailOTPService emailOTPService,
                       UserRepository userRepository){
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.pendingRegistrationRepository = pendingRegistrationRepository;
        this.emailOTPService = emailOTPService;
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest req){

        //Authenticate new user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        //Generate JWT Token
        String token = jwtService.generateToken(userDetails);
        Long id = userDetails.getId();
        String email = userDetails.getEmail();
        String username = userDetails.getUsername();
        String firstName = userDetails.getFirstName();
        String lastName = userDetails.getLastName();


        return new LoginResponse(id,token,email,username,firstName,lastName);

    }

        //Register using pending registration
        @Transactional
        public RegisterResponse verify(EmailVerifyRequest req){

            PendingRegistration pendingRegistration = pendingRegistrationRepository.findByEmail(req.getEmail());

            if (pendingRegistration == null){
                throw new RuntimeException("no email found");
            }

            if (!LocalDateTime.now().isBefore(pendingRegistration.getExpiresAt())) {
                pendingRegistrationRepository.delete(pendingRegistration);
                throw new RuntimeException("Verification code expired");
            }

            //verify
            if (!passwordEncoder.matches(
                    req.getCode(),
                    pendingRegistration.getVerificationCodeHash())) {

                throw new RuntimeException("Invalid verification code");
            }



            User user = new User();

            //create user
            user.setFirstName(pendingRegistration.getFirstName());
            user.setLastName(pendingRegistration.getLastName());
            user.setUsername(pendingRegistration.getUsername());
            user.setEmail(pendingRegistration.getEmail());
            user.setRole(UserRole.USER);
            user.setPassword(pendingRegistration.getPasswordHash());

            //Save new user
            userService.addUser(user);

            // Remove pending registration
            pendingRegistrationRepository.delete(pendingRegistration);

            RegisterResponse resp = new RegisterResponse(user.getFirstName(), user.getLastName(), user.getEmail());
            return resp;
        }


        //Add to registration
        public EmailVerifyRespond register(RegisterRequest registerRequest){

            User user = userRepository.findByEmail(registerRequest.getEmail());

            if (user != null){
                throw new RuntimeException("User already exists!");
            }
            PendingRegistration registration = pendingRegistrationRepository.findByEmail(registerRequest.getEmail());

            if (registration != null){
                pendingRegistrationRepository.delete(registration);
            }

            PendingRegistration pendingRegistration = new PendingRegistration();
            pendingRegistration.setEmail(registerRequest.getEmail());
            pendingRegistration.setFirstName(registerRequest.getFirstName());
            pendingRegistration.setLastName(registerRequest.getLastName());
            pendingRegistration.setUsername(registerRequest.getUsername());
            pendingRegistration.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

            LocalDateTime now = LocalDateTime.now();

            pendingRegistration.setCreatedAt(now);
            pendingRegistration.setExpiresAt(now.plusMinutes(5));

            //Generate code
            String code = generateVerificationCode();
            pendingRegistration.setVerificationCodeHash(passwordEncoder.encode(code));


            pendingRegistrationRepository.save(pendingRegistration);

            emailOTPService.sendOtpEmail(
                    pendingRegistration.getEmail(),
                    code
            );

            EmailVerifyRespond emailVerifyRespond = new EmailVerifyRespond(pendingRegistration.getEmail());
            return emailVerifyRespond;
        }


        private String generateVerificationCode() {
            return String.valueOf(
                    ThreadLocalRandom.current().nextInt(100000, 1000000)
            );
        }

}

