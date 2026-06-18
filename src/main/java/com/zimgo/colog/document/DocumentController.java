package com.zimgo.colog.document;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/document")
public class DocumentController {

    public DocumentService documentService;

    public DocumentController(
            DocumentService documentService
    ) {
        this.documentService = documentService;
    }

    @GetMapping("/{diaryId}/all")
    public ResponseEntity<?> getDiaryDocuments(
            @PathVariable Long diaryId
    ) {

        return ResponseEntity.ok(
                documentService.getDiaryDocuments(diaryId)
        );
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<?> getDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(documentService.getDocument(documentId));
    }
}