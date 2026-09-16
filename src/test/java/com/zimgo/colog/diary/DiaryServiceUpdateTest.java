package com.zimgo.colog.diary;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.diary.dto.DiaryCollaboratorRequest;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceUpdateTest {

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
    void addCollaborator_shouldAddUser_whenCurrentUserIsOwner() {

        when(diaryRepository.findById(100L))
                .thenReturn(java.util.Optional.of(diary));

        when(userService.getUserById(2L))
                .thenReturn(collaborator);

        diaryService.addCollaborator(100L, new DiaryCollaboratorRequest("mail@sample.com"));

        assertTrue(
                diary.getCollaborators()
                        .contains(collaborator)
        );

        verify(diaryRepository)
                .save(diary);
    }

    @Test
    void addCollaborator_shouldNotDuplicateUser() {

        diary.getCollaborators().add(collaborator);

        when(diaryRepository.findById(100L))
                .thenReturn(java.util.Optional.of(diary));

        when(userService.getUserById(2L))
                .thenReturn(collaborator);

        diaryService.addCollaborator(100L, new DiaryCollaboratorRequest("mail@sample.com"));

        assertEquals(
                1,
                diary.getCollaborators().size()
        );

        verify(diaryRepository)
                .save(diary);
    }

    @Test
    void addCollaborator_shouldReject_whenCurrentUserIsNotOwner() {

        mockAuthenticatedUser(2L);

        when(diaryRepository.findById(100L))
                .thenReturn(java.util.Optional.of(diary));

        assertThrows(
                AccessDeniedException.class,
                () -> diaryService.addCollaborator(100L, new DiaryCollaboratorRequest("mail@sample.com"))
        );

        verify(diaryRepository, never())
                .save(any(Diary.class));
    }

    @Test
    void removeCollaborator_shouldRemoveUser_whenCurrentUserIsOwner() {

        diary.getCollaborators().add(collaborator);

        when(diaryRepository.findById(100L))
                .thenReturn(java.util.Optional.of(diary));

        when(userService.getUserById(2L))
                .thenReturn(collaborator);

        diaryService.removeCollaborator(100L, new DiaryCollaboratorRequest());

        assertFalse(
                diary.getCollaborators()
                        .contains(collaborator)
        );

        verify(diaryRepository)
                .save(diary);
    }

    @Test
    void removeCollaborator_shouldReject_whenCurrentUserIsNotOwner() {

        mockAuthenticatedUser(2L);

        diary.getCollaborators().add(collaborator);

        when(diaryRepository.findById(100L))
                .thenReturn(java.util.Optional.of(diary));

        assertThrows(
                AccessDeniedException.class,
                () -> diaryService.removeCollaborator(100L, new DiaryCollaboratorRequest("mail@sample.com"))
        );

        verify(diaryRepository, never())
                .save(any(Diary.class));
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
