package com.zimgo.colog.document;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.document.dto.DocumentRequest;
import com.zimgo.colog.document.dto.DocumentResponse;
import com.zimgo.colog.revision.Revision;
import com.zimgo.colog.revision.RevisionService;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    private final DiaryService diaryService;
    private final RevisionService revisionService;
    public DocumentRepository documentRepository;

    public DocumentService(DocumentRepository documentRepository, DiaryService diaryService, RevisionService revisionService) {
        this.documentRepository = documentRepository;
        this.diaryService = diaryService;
        this.revisionService = revisionService;
    }

    // Functions

    //IMPORTANT! For mock data only
    public Document createDocument(Document document) throws IOException {
        Document savedDoc = documentRepository.save(document);

        return savedDoc;
    }

    //CREATE document
    public Document createDocument(Long diaryId, DocumentRequest req){
        Diary diary = diaryService.getAccessibleDiary(diaryId);
        Document doc = new Document();
        doc.setDiary(diary);
        doc.setDate(req.getDate());

        return documentRepository.save(doc);
    }


    //GET all document
    public List<Document>  getAllDocuments(Long diaryId){
        diaryService.getAccessibleDiary(diaryId);

        return documentRepository.findAllDocumentsByDiaryId(diaryId);
    }

    //GET document by diary id and document id
    public Document getDocument(Long diaryId, Long documentId) {
        diaryService.getAccessibleDiary(diaryId);

        return documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId, documentId)
                .orElseThrow(
                        () -> new RuntimeException("Document does not exist")
                );
    }

    public DocumentResponse editDocument(Long diaryId, Long documentId, DocumentRequest req) throws IOException {
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId, documentId)
                .orElseThrow(() -> new RuntimeException("Document does not exist"));
        document.setDate(req.getDate());

        documentRepository.save(document);

        return new DocumentResponse(document.getDate());
    }


    //DELETE DOCUMENT
    public void deleteDocument(Long diaryId, Long documentId) {
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId,documentId)
                .orElseThrow(() -> new RuntimeException("Document does not exist"));

        documentRepository.delete(document);
    }

    //SAVE yjs
    public void saveYjsState(Long diaryId, Long documentId, byte[] state){
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId,documentId)
                .orElseThrow(() -> new RuntimeException("Document does not exist"));

        document.setYjsState(state);

        documentRepository.save(document);

    }
    //RETRIEVE yjs
    public byte[] getYjsState(Long diaryId, Long documentId){
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId,documentId)
                .orElseThrow(() -> new RuntimeException("Document does not exist"));

        return document.getYjsState();
    }
    public void saveVersionSnapshot(Long documentId){
              Document doc = documentRepository.findById(documentId).orElseThrow();

              byte[] state = doc.getYjsState();
              revisionService.createRevision(documentId, state);
    }

}