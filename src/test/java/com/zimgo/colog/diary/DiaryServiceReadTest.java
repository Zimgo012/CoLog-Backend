
package com.zimgo.colog.diary;

import com.zimgo.colog.auth.security.CustomUserDetails;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceReadTest {

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
    private User collaborator;
    private Diary diary;

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

        collaborator = new User();
        collaborator.setUserId(2L);
        collaborator.setFirstName("Jane");
        collaborator.setLastName("Doe");

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
    void getAccessibleDiary_shouldReturnDiary_whenUserIsOwner() {

        when(diaryRepository.findById(100L))
                .thenReturn(Optional.of(diary));

        Diary result =
                diaryService.getAccessibleDiary(100L);

        assertEquals(diary, result);

        verify(diaryRepository)
                .findById(100L);
    }

    @Test
    void getAccessibleDiary_shouldReturnDiary_whenUserIsCollaborator() {

        diary.getCollaborators().add(collaborator);

        mockAuthenticatedUser(2L);

        when(diaryRepository.findById(100L))
                .thenReturn(Optional.of(diary));

        Diary result =
                diaryService.getAccessibleDiary(100L);

        assertEquals(diary, result);
    }

    @Test
    void getAccessibleDiary_shouldThrowAccessDenied_whenUserHasNoAccess() {

        mockAuthenticatedUser(3L);

        when(diaryRepository.findById(100L))
                .thenReturn(Optional.of(diary));

        assertThrows(
                AccessDeniedException.class,
                () -> diaryService.getAccessibleDiary(100L)
        );
    }

    @Test
    void getAccessibleDiary_shouldThrowException_whenDiaryDoesNotExist() {

        when(diaryRepository.findById(100L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> diaryService.getAccessibleDiary(100L)
        );
    }

    @Test
    void getMyDiaries_shouldReturnOwnedDiaries() {

        Diary secondDiary = new Diary();
        secondDiary.setDiaryId(101L);
        secondDiary.setOwner(owner);

        List<Diary> diaries =
                List.of(diary, secondDiary);

        when(diaryRepository.findAllByOwnerUserId(1L))
                .thenReturn(diaries);

        List<DiaryResponse> result =
                diaryService.getMyDiaries();

        assertEquals(2, result.size());
        assertEquals(diaries, result);

        verify(diaryRepository)
                .findAllByOwnerUserId(1L);
    }

    @Test
    void getAllCollaboratedDiaries_shouldReturnCollaboratedDiaries() {

        mockAuthenticatedUser(2L);

        diary.getCollaborators().add(collaborator);

        List<Diary> diaries =
                List.of(diary);

        when(diaryRepository.findAllByCollaboratorsUserId(2L))
                .thenReturn(diaries);

        List<DiaryResponse> result =
                diaryService.getAllCollaboratedDiaries();

        assertEquals(1, result.size());
        assertEquals(diary, result.get(0));

        verify(diaryRepository)
                .findAllByCollaboratorsUserId(2L);
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

