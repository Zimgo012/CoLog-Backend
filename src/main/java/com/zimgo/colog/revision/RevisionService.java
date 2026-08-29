package com.zimgo.colog.revision;

import com.zimgo.colog.document.Document;
import com.zimgo.colog.document.DocumentRepository;
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
        Document doc = documentRepository.findById(documentId).orElseThrow();

        Revision revision = new Revision();

        revision.setDocument(doc);
        revision.setYjsUpdate(state);

        revisionRepository.save(revision);

    }

    public Revision getRevision(Long documentId, Long revisionId) {

        Revision revision = revisionRepository.findById(revisionId)
                .orElseThrow();

        if (!revision.getDocument().getDocumentId().equals(documentId)) {
            throw new IllegalArgumentException(
                    "Revision does not belong to this document"
            );
        }

        return revision;
    }

    public List<Revision> getDocumentRevisions(Long documentId) {
        return revisionRepository.findAllByDocumentDocumentId(documentId);
    }

}
