package com.zimgo.colog.revision;

import com.zimgo.colog.document.Document;
import com.zimgo.colog.document.DocumentRepository;
import com.zimgo.colog.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RevisionService {

    public final RevisionRepository revisionRepository;
    public final DocumentRepository documentRepository;

    public RevisionService(RevisionRepository revisionRepository, DocumentRepository documentRepository){
        this.revisionRepository = revisionRepository;
        this.documentRepository = documentRepository;
    }
    public void createRevision(Long documentId, byte[] state){
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "DOCUMENT_NOT_FOUND",
                        "Document not found"
                ));

        Revision revision = new Revision();

        revision.setDocument(doc);
        revision.setYjsUpdate(state);

        revisionRepository.save(revision);

    }

    public Revision getRevision(Long documentId, Long revisionId) {

        Revision revision = revisionRepository.findById(revisionId)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "REVISION_NOT_FOUND",
                        "Revision not found"
                ));

        if (!revision.getDocument().getDocumentId().equals(documentId)) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "REVISION_DOCUMENT_MISMATCH",
                    "Revision does not belong to this document"
            );
        }

        return revision;
    }

    public List<Revision> getDocumentRevisions(Long documentId) {
        return revisionRepository.findAllByDocumentDocumentId(documentId);
    }

}
