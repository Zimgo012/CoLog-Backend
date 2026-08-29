package com.zimgo.colog.document;

import com.zimgo.colog.revision.Revision;
import com.zimgo.colog.revision.RevisionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/document")
public class DocumentController {

    private final RevisionService revisionService;
    public DocumentService documentService;

    public DocumentController(
            DocumentService documentService,
            RevisionService revisionService) {
        this.documentService = documentService;
        this.revisionService = revisionService;
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