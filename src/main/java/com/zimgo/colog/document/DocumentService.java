package com.zimgo.colog.document;

import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
public class DocumentService {
    public DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }
    public void editDocument(Long diaryId, Long documentId, String content) throws IOException {
        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId, documentId).orElseThrow();

        //TODO : save metadatas here

        //Saving the file
        saveFile(diaryId, documentId, content);

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

    private void saveFile(Long diaryId, Long documentId, String content) throws IOException {

        Path directory = Paths.get("storage", "diaryId-" + diaryId);
        Files.createDirectories(directory);
        Path filePath = directory.resolve("document-" + documentId + ".txt");
        Files.writeString(filePath, content, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
