package com.zimgo.colog.document;

import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.revision.RevisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceRevisionTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private RevisionService revisionService;

    @Mock
    private DiaryService diaryService;

    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        documentService = new DocumentService(
                documentRepository,
                diaryService,
                revisionService
        );
    }

    @Test
    void saveVersionSnapshot_shouldCreateRevisionFromCurrentYjsState() {

        // Arrange
        Long documentId = 1L;
        byte[] state = new byte[]{1, 2, 3};

        Document document = new Document();
        document.setDocumentId(documentId);
        document.setYjsState(state);

        when(documentRepository.findById(documentId))
                .thenReturn(Optional.of(document));

        // Act
        documentService.saveVersionSnapshot(documentId);

        // Assert
        verify(documentRepository).findById(documentId);

        verify(revisionService)
                .createRevision(documentId, state);
    }
}