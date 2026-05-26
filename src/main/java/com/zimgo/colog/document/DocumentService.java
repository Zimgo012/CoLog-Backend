package com.zimgo.colog.document;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {
    public DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    public void editDocument(Long DiaryId, Long DocumentId, String content) {
        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(DiaryId, DocumentId).orElseThrow();

        document.setContent(content);
        documentRepository.save(document);
    }

    public void deleteDocument(Document document) {

    }

    public void addDocument(Document document) {

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
}
