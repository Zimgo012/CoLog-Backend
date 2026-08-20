package com.zimgo.colog.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceDeleteTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {

        userService = new UserService(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void deleteUser_shouldDeleteUser() {

        userService.deleteUser(1L);

        verify(userRepository)
                .deleteById(1L);
    }

    @Test
    void deleteUser_shouldCallRepositoryOnlyOnce() {

        userService.deleteUser(1L);

        verify(userRepository, times(1))
                .deleteById(1L);
    }
}