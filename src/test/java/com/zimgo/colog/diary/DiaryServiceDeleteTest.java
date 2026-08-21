package com.zimgo.colog.diary;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceDeleteTest {

    @Mock
    private DiaryRepository diaryRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private CustomUserDetails userDetails;

    private DiaryService diaryService;

    private User owner;
    private User collaborator;
    private Diary diary;

    @BeforeEach
    void setUp() {

        diaryService = new DiaryService(
                diaryRepository,
                userService
        );

        owner = new User();
        owner.setUserId(1L);

        collaborator = new User();
        collaborator.setUserId(2L);

        diary = new Diary();
        diary.setDiaryId(100L);
        diary.setTitle("My Diary");
        diary.setOwner(owner);
        diary.setCollaborators(new ArrayList<>());

        mockAuthenticatedUser(1L);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deleteDiary_shouldDelete_whenCurrentUserIsOwner() {

        when(diaryRepository.findById(100L))
                .thenReturn(Optional.of(diary));

        diaryService.deleteDiary(100L);

        verify(diaryRepository)
                .delete(diary);
    }

    @Test
    void deleteDiary_shouldReject_whenCurrentUserIsCollaborator() {

        mockAuthenticatedUser(2L);

        diary.getCollaborators()
                .add(collaborator);

        when(diaryRepository.findById(100L))
                .thenReturn(Optional.of(diary));

        assertThrows(
                AccessDeniedException.class,
                () -> diaryService.deleteDiary(100L)
        );

        verify(diaryRepository, never())
                .delete(any(Diary.class));
    }

    @Test
    void deleteDiary_shouldReject_whenDiaryDoesNotExist() {

        when(diaryRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> diaryService.deleteDiary(100L)
        );

        verify(diaryRepository, never())
                .delete(any(Diary.class));
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
