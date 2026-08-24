package com.zimgo.colog.document;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceReadTest {

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
    // GET ALL DOCUMENTS
    // =========================================================

    @Test
    void getAllDocuments_shouldReturnDiaryDocuments() {

        Long diaryId = 1L;

        Diary diary = new Diary();

        Document document1 = new Document();
        Document document2 = new Document();

        List<Document> documents =
                List.of(document1, document2);

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(diary);

        when(documentRepository
                .findAllDocumentsByDiaryId(diaryId))
                .thenReturn(documents);

        List<Document> result =
                documentService.getAllDocuments(diaryId);

        assertEquals(2, result.size());
        assertEquals(documents, result);

        verify(diaryService)
                .getAccessibleDiary(diaryId);

        verify(documentRepository)
                .findAllDocumentsByDiaryId(diaryId);
    }

    @Test
    void getAllDocuments_shouldNotQueryRepository_whenUserHasNoAccess() {

        Long diaryId = 1L;

        when(diaryService.getAccessibleDiary(diaryId))
                .thenThrow(new RuntimeException("User not authorized"));

        assertThrows(
                RuntimeException.class,
                () -> documentService.getAllDocuments(diaryId)
        );

        verify(documentRepository, never())
                .findAllDocumentsByDiaryId(anyLong());
    }

    // =========================================================
    // GET DOCUMENT
    // =========================================================

    @Test
    void getDocument_shouldReturnDocument() {

        Long diaryId = 1L;
        Long documentId = 10L;

        Diary diary = new Diary();

        Document document = new Document();
        document.setDiary(diary);

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(diary);

        when(documentRepository
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                ))
                .thenReturn(Optional.of(document));

        Document result =
                documentService.getDocument(
                        diaryId,
                        documentId
                );

        assertNotNull(result);
        assertEquals(document, result);

        verify(diaryService)
                .getAccessibleDiary(diaryId);

        verify(documentRepository)
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                );
    }

    @Test
    void getDocument_shouldThrow_whenDocumentDoesNotExist() {

        Long diaryId = 1L;
        Long documentId = 10L;

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(new Diary());

        when(documentRepository
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                ))
                .thenReturn(Optional.empty());

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> documentService.getDocument(
                                diaryId,
                                documentId
                        )
                );

        assertEquals(
                "Document does not exist",
                exception.getMessage()
        );
    }

    // =========================================================
    // GET YJS STATE
    // =========================================================

    @Test
    void getYjsState_shouldReturnState() {

        Long diaryId = 1L;
        Long documentId = 10L;

        byte[] state = new byte[]{1, 2, 3};

        Document document = new Document();
        document.setYjsState(state);

        when(diaryService.getAccessibleDiary(diaryId))
                .thenReturn(new Diary());

        when(documentRepository
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                ))
                .thenReturn(Optional.of(document));

        byte[] result =
                documentService.getYjsState(
                        diaryId,
                        documentId
                );

        assertArrayEquals(state, result);

        verify(documentRepository)
                .findDocumentByDiaryIdAndDocumentId(
                        diaryId,
                        documentId
                );
    }
}
