package com.zimgo.colog.document;

import com.zimgo.colog.revision.RevisionService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    private final RevisionService revisionService;
    public DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository, RevisionService revisionService) {
        this.documentRepository = documentRepository;
        this.revisionService = revisionService;
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


    //Initial yjs-state
    public void saveYjsState(Long documentId, byte[] state){
        Document document = documentRepository.findById(documentId).orElseThrow();

        document.setYjsState(state);
        saveDocument(document);
    }

    public byte[] getYjsState(Long documentId){
        Document document = documentRepository.findById(documentId).orElseThrow();
        return document.getYjsState();
    }

    public void saveVersionSnapshot(Long documentId){
        Document doc = documentRepository.findById(documentId).orElseThrow();

        byte[] state = doc.getYjsState();
        revisionService.createRevision(documentId, state);
    }




}
