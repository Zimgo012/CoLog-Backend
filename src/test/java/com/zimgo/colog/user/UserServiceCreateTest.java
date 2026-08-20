package com.zimgo.colog.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceCreateTest {

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
    void addUser_shouldSaveUser_whenUserDoesNotExist() {

        User user = createUser();

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);

        when(userRepository.save(user))
                .thenReturn(user);

        User result = userService.addUser(user);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository)
                .existsByEmail(user.getEmail());

        verify(userRepository)
                .save(user);

        verify(userRepository, never())
                .existByFirstName(anyString());
    }

    @Test
    void addUser_shouldThrowException_whenUserAlreadyExists() {

        User user = createUser();

        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        when(userRepository.existByFirstName(user.getFirstName()))
                .thenReturn(true);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.addUser(user)
        );

        assertEquals(
                "User exist!",
                exception.getMessage()
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    private User createUser() {
        return new User(
                null,
                "John",
                "Doe",
                "johndoe",
                "john@mail.com",
                "password",
                UserRole.USER,
                null,
                null
        );
    }
}