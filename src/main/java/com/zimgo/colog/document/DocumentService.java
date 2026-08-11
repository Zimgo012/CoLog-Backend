package com.zimgo.colog.document;

import com.zimgo.colog.deltaLog.DeltaLogService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {
    private final DeltaLogService deltaLogService;
    public DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository, DeltaLogService deltaLogService) {
        this.documentRepository = documentRepository;
        this.deltaLogService = deltaLogService;
    }

    // Functions
    public void editDocument(Long diaryId, Long documentId, String content) throws IOException {
        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId, documentId).orElseThrow();
        documentRepository.save(document);
    }

    public void deleteDocument(Document document) {
        documentRepository.delete(document);
    }

    public Document createDocument(Document document) throws IOException {
        Document savedDoc = documentRepository.save(document);

        deltaLogService.createInitialLog(document);
        return savedDoc;
    }


    public Document getDocument(Long DocumentId) {
        return documentRepository.findById(DocumentId).orElseThrow();
    }

    public List<Document>  getAllDocuments(){
        return documentRepository.findAll();
    }

    public List<Document> getDiaryDocuments(Long diaryId) {
        return documentRepository.findAllDocumentsByDiaryId(diaryId);
    }

    public void saveDocument(Document document) {
        documentRepository.save(document);
    }

    public void incrementDocRevision(Document doc){
        doc.setCurrentRevision(doc.getCurrentRevision() + 1);
    }


}
