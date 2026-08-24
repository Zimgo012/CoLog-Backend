package com.zimgo.colog.document;

import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.document.dto.DocumentRequest;
import com.zimgo.colog.document.dto.DocumentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceUpdateTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private DiaryService diaryService;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(
                documentRepository,
                diaryService
        );
    }

    // =========================================================
    // EDIT DOCUMENT
    // =========================================================

    @Test
    void editDocument_shouldUpdateDocument() throws IOException {

        Long diaryId = 1L;
        Long documentId = 10L;

        LocalDateTime newDate =
                LocalDate.of(2026, 8, 24).atStartOfDay();

        DocumentRequest request =
                new DocumentRequest();

        request.setDate(newDate);

        Document document =
                new Document();

        document.setDate(
                LocalDate.of(2026, 8, 20).atStartOfDay()
        );

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(new com.zimgo.colog.diary.Diary());

        when(documentRepository
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                ))
                .thenReturn(Optional.of(document));

        DocumentResponse result =
                documentService.editDocument(
                        diaryId,
                        documentId,
                        request
                );

        assertNotNull(result);
        assertEquals(newDate, document.getDate());

        verify(documentRepository)
                .save(document);
    }

    @Test
    void editDocument_shouldThrow_whenDocumentDoesNotExist()
            throws IOException {

        Long diaryId = 1L;
        Long documentId = 10L;

        DocumentRequest request =
                new DocumentRequest();

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(new com.zimgo.colog.diary.Diary());

        when(documentRepository
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                ))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> documentService.editDocument(
                        diaryId,
                        documentId,
                        request
                )
        );

        verify(documentRepository, never())
                .save(any(Document.class));
    }

    // =========================================================
    // SAVE YJS STATE
    // =========================================================

    @Test
    void saveYjsState_shouldUpdateState() {

        Long diaryId = 1L;
        Long documentId = 10L;

        byte[] state =
                new byte[]{10, 20, 30};

        Document document =
                new Document();

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(new com.zimgo.colog.diary.Diary());

        when(documentRepository
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                ))
                .thenReturn(Optional.of(document));

        documentService.saveYjsState(
                diaryId,
                documentId,
                state
        );

        assertArrayEquals(
                state,
                document.getYjsState()
        );

        verify(documentRepository)
                .save(document);
    }

    @Test
    void saveYjsState_shouldThrow_whenDocumentDoesNotExist() {

        Long diaryId = 1L;
        Long documentId = 10L;

        byte[] state =
                new byte[]{1, 2, 3};

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(new com.zimgo.colog.diary.Diary());

        when(documentRepository
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                ))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> documentService.saveYjsState(
                        diaryId,
                        documentId,
                        state
                )
        );

        verify(documentRepository, never())
                .save(any(Document.class));
    }

    @Test
    void saveYjsState_shouldNotModifyDocument_whenUserHasNoAccess() {

        Long diaryId = 1L;
        Long documentId = 10L;

        byte[] state =
                new byte[]{1, 2, 3};

        when(diaryService.getAccessibleDiary(diaryId))
                .thenThrow(
                        new RuntimeException("User not authorized")
                );

        assertThrows(
                RuntimeException.class,
                () -> documentService.saveYjsState(
                        diaryId,
                        documentId,
                        state
                )
        );

        verify(documentRepository, never())
                .findDocumentByDiaryIdAndDocumentId(
                        anyLong(),
                        anyLong()
                );

        verify(documentRepository, never())
                .save(any(Document.class));
    }
}
