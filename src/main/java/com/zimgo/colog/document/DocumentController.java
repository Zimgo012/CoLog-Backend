package com.zimgo.colog.document;

import com.zimgo.colog.document.dto.DocumentRequest;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/document")
public class DocumentController {

    public DocumentService documentService;

    public DocumentController(
            DocumentService documentService
    ) {
        this.documentService = documentService;
    }

    //CREATE a document
    @PostMapping("/{diaryId}/")
    public ResponseEntity<?> createDocument(@PathVariable Long diaryId,
                                            @RequestBody DocumentRequest req){
        return ResponseEntity.ok(documentService.createDocument(diaryId,req));
    }

    //GET all document in that diary
    @GetMapping("/{diaryId}/all")
    public ResponseEntity<?> getDiaryDocuments(@PathVariable Long diaryId) {
        return ResponseEntity.ok(
                documentService.getAllDocuments(diaryId)
        );
    }

    //GET a document
    @GetMapping("/{diaryId}/{documentId}")
    public ResponseEntity<?> getDocument(@PathVariable Long diaryId,
                                         @PathVariable Long documentId) {
        return ResponseEntity.ok(documentService.getDocument(diaryId, documentId));
    }

    //UPDATE a document
    @PatchMapping("/{diaryId}/{documentId}")
    public ResponseEntity<?> editDocument(@PathVariable Long diaryId,
                                          @PathVariable Long documentId,
                                          @RequestBody DocumentRequest req) throws IOException {
        return ResponseEntity.ok(documentService.editDocument(diaryId,documentId, req));
    }

    //DELETE a document
    @DeleteMapping("/{diaryId}/{documentId}")
    public ResponseEntity deleteDocument(@PathVariable Long diaryId,
                                            @PathVariable Long documentId){

        documentService.deleteDocument(diaryId,documentId);
        return ResponseEntity.ok().build();
    }

    /** RETRIEVE yjs state
     * Used to update yjs state from server to client
     * @return
     */
    @GetMapping( "/{diaryId}/{documentId}/yjs")
    public ResponseEntity<?> getState(@PathVariable Long diaryId,
                                      @PathVariable Long documentId){
        return ResponseEntity.ok(documentService.getYjsState(diaryId, documentId));
    }

    /** SAVE yjs state
     * Used to update yjs state from client to server
     * @return
     */
    @PostMapping(value = "/{diaryId}/{documentId}/yjs",
            consumes = "application/octet-stream")
    public ResponseEntity<?> saveState(@PathVariable Long diaryId,
                                       @PathVariable Long documentId,
                                       @RequestBody byte[] update){
      documentService.saveYjsState(diaryId, documentId, update);

      return ResponseEntity.ok().build();
    }
}