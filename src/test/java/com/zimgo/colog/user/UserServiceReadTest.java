package com.zimgo.colog.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceReadTest {

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

    // =========================
    // GET ALL USERS
    // =========================

    @Test
    void getAllUsers_shouldReturnAllUsers() {

        User john = createUser(
                1L,
                "John",
                "Doe",
                "johndoe",
                "john@mail.com"
        );

        User jane = createUser(
                2L,
                "Jane",
                "Doe",
                "janedoe",
                "jane@mail.com"
        );

        when(userRepository.findAll())
                .thenReturn(List.of(john, jane));

        List<User> result =
                userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Jane", result.get(1).getFirstName());

        verify(userRepository).findAll();
    }

    // =========================
    // GET BY ID
    // =========================

    @Test
    void getUserById_shouldReturnUser_whenUserExists() {

        User john = createUser(
                1L,
                "John",
                "Doe",
                "johndoe",
                "john@mail.com"
        );

        when(userRepository.existsById(1L))
                .thenReturn(true);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(john));

        User result =
                userService.getUserById(1L);

        assertEquals(john, result);

        verify(userRepository)
                .existsById(1L);

        verify(userRepository)
                .findById(1L);
    }

    @Test
    void getUserById_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.existsById(1L))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.getUserById(1L)
                );

        assertEquals(
                "User does not exist!",
                exception.getMessage()
        );

        verify(userRepository, never())
                .findById(1L);
    }

    // =========================
    // GET BY EMAIL
    // =========================

    @Test
    void getUserByEmail_shouldReturnUser_whenUserExists() {

        User john = createUser(
                1L,
                "John",
                "Doe",
                "johndoe",
                "john@mail.com"
        );

        when(userRepository.existsByEmail("john@mail.com"))
                .thenReturn(true);

        when(userRepository.findByEmail("john@mail.com"))
                .thenReturn(john);

        User result =
                userService.getUserByEmail("john@mail.com");

        assertEquals(john, result);

        verify(userRepository)
                .existsByEmail("john@mail.com");

        verify(userRepository)
                .findByEmail("john@mail.com");
    }

    @Test
    void getUserByEmail_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.existsByEmail("john@mail.com"))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.getUserByEmail(
                                "john@mail.com"
                        )
                );

        assertEquals(
                "User does not exist",
                exception.getMessage()
        );

        verify(userRepository, never())
                .findByEmail(anyString());
    }

    // =========================
    // GET BY USERNAME
    // =========================

    @Test
    void getUserByUsername_shouldReturnUser_whenUserExists() {

        User john = createUser(
                1L,
                "John",
                "Doe",
                "johndoe",
                "john@mail.com"
        );

        when(userRepository.existByUsername("johndoe"))
                .thenReturn(true);

        when(userRepository.findByUsername("johndoe"))
                .thenReturn(john);

        User result =
                userService.getUserByUsername("johndoe");

        assertEquals(john, result);

        verify(userRepository)
                .existByUsername("johndoe");

        verify(userRepository)
                .findByUsername("johndoe");
    }

    @Test
    void getUserByUsername_shouldThrowException_whenUserDoesNotExist() {

        when(userRepository.existByUsername("johndoe"))
                .thenReturn(false);

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.getUserByUsername(
                                "johndoe"
                        )
                );

        assertEquals(
                "User does not exist",
                exception.getMessage()
        );

        verify(userRepository, never())
                .findByUsername(anyString());
    }

    private User createUser(
            Long id,
            String firstName,
            String lastName,
            String username,
            String email
    ) {
        return new User(
                id,
                firstName,
                lastName,
                username,
                email,
                "password",
                UserRole.USER,
                null,
                null
        );
    }
}