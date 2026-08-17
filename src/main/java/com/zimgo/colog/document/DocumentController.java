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

    /**
     * Used to update yjs state from server to client
     * @return
     */
    @GetMapping( "/{documentId}/yjs")
    public ResponseEntity<?> getState(@PathVariable Long documentId){
        return ResponseEntity.ok(documentService.getYjsState(documentId));
    }

    /**
     * Used to update yjs state from client to server
     * @return
     */
    @PostMapping(value = "/{documentId}/yjs",
            consumes = "application/octet-stream")
    public ResponseEntity<?> saveState(@PathVariable Long documentId,
                                       @RequestBody byte[] update){
      documentService.saveYjsState(documentId, update);

      return ResponseEntity.ok().build();
    }
}