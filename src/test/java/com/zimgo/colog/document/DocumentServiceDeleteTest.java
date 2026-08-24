package com.zimgo.colog.document;

import com.zimgo.colog.diary.DiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceDeleteTest {

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
    void deleteDocument_shouldDeleteDocument() {

        Long diaryId = 1L;
        Long documentId = 10L;

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

        documentService.deleteDocument(
                diaryId,
                documentId
        );

        verify(documentRepository)
                .delete(document);
    }

    @Test
    void deleteDocument_shouldThrow_whenDocumentDoesNotExist() {

        Long diaryId = 1L;
        Long documentId = 10L;

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
                () -> documentService.deleteDocument(
                        diaryId,
                        documentId
                )
        );

        verify(documentRepository, never())
                .delete(any(Document.class));
    }

    @Test
    void deleteDocument_shouldNotDelete_whenUserHasNoAccess() {

        Long diaryId = 1L;
        Long documentId = 10L;

        when(diaryService.getAccessibleDiary(diaryId))
                .thenThrow(
                        new RuntimeException("User not authorized")
                );

        assertThrows(
                RuntimeException.class,
                () -> documentService.deleteDocument(
                        diaryId,
                        documentId
                )
        );

        verify(documentRepository, never())
                .findDocumentByDiaryIdAndDocumentId(
                        anyLong(),
                        anyLong()
                );

        verify(documentRepository, never())
                .delete(any(Document.class));
    }
}