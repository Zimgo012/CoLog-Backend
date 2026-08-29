package com.zimgo.colog.document;

import com.zimgo.colog.document.dto.DocumentRequest;
import com.zimgo.colog.revision.Revision;
import com.zimgo.colog.revision.RevisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentController {

    public DocumentService documentService;
    public RevisionService revisionService;

    public DocumentController(
            DocumentService documentService,
            RevisionService revisionService
    ) {
        this.documentService = documentService;
        this.revisionService = revisionService;
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

    //this would be an SSE on the future
    @GetMapping("/{documentId}/revisions")
    public ResponseEntity<List<Revision>>  getRevisions(
            @PathVariable Long documentId){
        return ResponseEntity.ok(revisionService.getDocumentRevisions(documentId));
    }

    @PostMapping("/{documentId}/revision/save")
    public ResponseEntity<?> saveStateSnapshot(@PathVariable Long documentId){

        documentService.saveVersionSnapshot(documentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{documentId}/revisions/{revisionId}")
    public ResponseEntity<Revision> viewRevision(
            @PathVariable Long documentId,
            @PathVariable Long revisionId) {

        return ResponseEntity.ok(revisionService.getRevision(documentId, revisionId));
    }
}