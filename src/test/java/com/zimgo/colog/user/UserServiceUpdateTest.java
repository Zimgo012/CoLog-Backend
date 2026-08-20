package com.zimgo.colog.user;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.user.dto.UserRequest;
import com.zimgo.colog.user.dto.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceUpdateTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    private UserService userService;

    @BeforeEach
    void setUp() {

        userService = new UserService(
                userRepository,
                passwordEncoder
        );

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }

    // =========================
    // SUCCESSFUL UPDATE
    // =========================

    @Test
    void editUser_shouldUpdateUser_whenEditingOwnAccount()
            throws Exception {

        User john = createUser();

        UserRequest request = new UserRequest();

        request.setFirstName("Johnny");
        request.setLastName("Smith");
        request.setEmail("johnny@mail.com");

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(john));

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getId())
                .thenReturn(1L);

        when(userRepository.save(john))
                .thenReturn(john);

        UserResponse result =
                userService.editUser(request, 1L);

        assertEquals(
                "Johnny",
                result.getFirstName()
        );

        assertEquals(
                "Smith",
                result.getLastName()
        );

        assertEquals(
                "johnny@mail.com",
                result.getEmail()
        );

        verify(userRepository)
                .save(john);
    }

    // =========================
    // CANNOT EDIT OTHER USER
    // =========================

    @Test
    void editUser_shouldThrowAccessDenied_whenEditingAnotherUser()
            throws Exception {

        User john = createUser();

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(john));

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        // Authenticated user = ID 2
        when(userDetails.getId())
                .thenReturn(2L);

        AccessDeniedException exception =
                assertThrows(
                        AccessDeniedException.class,
                        () -> userService.editUser(
                                new UserRequest(),
                                1L
                        )
                );

        assertEquals(
                "Can only edit your own account",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =========================
    // USER DOES NOT EXIST
    // =========================

    @Test
    void editUser_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                RuntimeException.class,
                () -> userService.editUser(
                        new UserRequest(),
                        1L
                )
        );

        verify(userRepository, never())
                .findById(1L);

        verify(userRepository, never())
                .save(any(User.class));
    }

    // =========================
    // PASSWORD UPDATE
    // =========================

    @Test
    void editUser_shouldEncodePassword_whenPasswordProvided()
            throws Exception {

        User john = createUser();

        UserRequest request = new UserRequest();
        request.setPassword("newPassword");

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(john));

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getId())
                .thenReturn(1L);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedPassword");

        when(userRepository.save(john))
                .thenReturn(john);

        userService.editUser(request, 1L);

        assertEquals(
                "encodedPassword",
                john.getPassword()
        );

        verify(passwordEncoder)
                .encode("newPassword");

        verify(userRepository)
                .save(john);
    }

    // =========================
    // NULL FIELDS
    // =========================

    @Test
    void editUser_shouldKeepExistingValues_whenFieldsAreNull()
            throws Exception {

        User john = createUser();

        UserRequest request = new UserRequest();

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(john));

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(userDetails.getId())
                .thenReturn(1L);

        when(userRepository.save(john))
                .thenReturn(john);

        UserResponse result =
                userService.editUser(request, 1L);

        assertEquals(
                "John",
                result.getFirstName()
        );

        assertEquals(
                "Doe",
                result.getLastName()
        );

        assertEquals(
                "johndoe@mail.com",
                result.getEmail()
        );

        assertEquals(
                "password",
                john.getPassword()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository)
                .save(john);
    }

    private User createUser() {

        return new User(
                1L,
                "John",
                "Doe",
                "johndoe",
                "johndoe@mail.com",
                "password",
                UserRole.USER,
                null,
                null
        );
    }
}