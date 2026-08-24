package com.zimgo.colog.document;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.document.dto.DocumentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceCreateTest {

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

    @Test
    void createDocument_shouldCreateDocument() {

        Long diaryId = 1L;

        Diary diary = new Diary();
        diary.setDiaryId(diaryId);

        DocumentRequest request = new DocumentRequest();
        request.setDate(LocalDate.of(2026, 8, 24).atStartOfDay());

        Document savedDocument = new Document();
        savedDocument.setDiary(diary);
        savedDocument.setDate(request.getDate());

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(diary);

        when(documentRepository.save(any(Document.class)))
                .thenReturn(savedDocument);

        Document result =
                documentService.createDocument(diaryId, request);

        assertNotNull(result);
        assertEquals(diary, result.getDiary());
        assertEquals(request.getDate(), result.getDate());

        verify(diaryService)
                .getAccessibleDiary(diaryId);

        verify(documentRepository)
                .save(any(Document.class));
    }

    @Test
    void createDocument_shouldVerifyDiaryAccessBeforeCreating() {

        Long diaryId = 1L;

        Diary diary = new Diary();

        DocumentRequest request = new DocumentRequest();
        request.setDate(LocalDate.of(2026, 8, 24).atStartOfDay());

        when(diaryService.getAccessibleDiary(diaryId))
                .thenThrow(new RuntimeException("User not authorized"));

        assertThrows(
                RuntimeException.class,
                () -> documentService.createDocument(
                        diaryId,
                        request
                )
        );

        verify(diaryService)
                .getAccessibleDiary(diaryId);

        verify(documentRepository, never())
                .save(any(Document.class));
    }
}
