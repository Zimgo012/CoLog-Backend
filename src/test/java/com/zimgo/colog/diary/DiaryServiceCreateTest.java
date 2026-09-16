package com.zimgo.colog.diary;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.diary.dto.DiaryRequest;
import com.zimgo.colog.diary.dto.DiaryResponse;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceCreateTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private CacheManager cacheManager;

    private DiaryService diaryService;

    private User owner;

    @BeforeEach
    void setUp() {

        diaryService = new DiaryService(
                diaryRepository,
                userService,
                cacheManager
        );

        owner = new User();
        owner.setUserId(1L);
        owner.setFirstName("John");
        owner.setLastName("Doe");
        owner.setUsername("johndoe");
        owner.setEmail("john@mail.com");
        owner.setPassword("password");

        mockAuthenticatedUser(1L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void addDiary_shouldCreateDiaryForAuthenticatedUser() {

        DiaryRequest request = new DiaryRequest();
        request.setTitle("My Diary");

        when(userService.getUserById(1L))
                .thenReturn(owner);

        when(diaryRepository.save(any(Diary.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        DiaryResponse response =
                diaryService.addDiary(request);

        assertNotNull(response);

        verify(userService)
                .getUserById(1L);

        verify(diaryRepository).save(
                argThat(diary ->
                        diary.getTitle().equals("My Diary")
                                && diary.getOwner().equals(owner)
                                && diary.getCreatedAt() != null
                                && diary.getLastOpenedAt() != null
                )
        );
    }

    private void mockAuthenticatedUser(Long userId) {

        when(userDetails.getId())
                .thenReturn(userId);

        when(authentication.isAuthenticated())
                .thenReturn(true);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
    }
}

