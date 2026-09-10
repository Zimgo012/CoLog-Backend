package com.zimgo.colog.document;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.diary.dto.DiaryResponse;
import com.zimgo.colog.document.dto.DocumentListResponse;
import com.zimgo.colog.document.dto.DocumentRequest;
import com.zimgo.colog.document.dto.DocumentResponse;
import com.zimgo.colog.exception.AppException;
import com.zimgo.colog.revision.Revision;
import com.zimgo.colog.revision.RevisionService;
import org.springframework.http.HttpStatus;
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
        byte[] state = new byte[]{1,3,4,5,6};
        revisionService.createRevision(savedDoc.getDocumentId(), state);


        return savedDoc;
    }

    //CREATE document
    public Document createDocument(Long diaryId, DocumentRequest req){
        Diary diary = diaryService.getAccessibleDiary(diaryId);
        Document doc = new Document();
        diary.getDocuments().add(doc);
        doc.setDiary(diary);
        doc.setDate(req.getDate());
        doc.setYjsState(new byte[]{});

        return documentRepository.save(doc);
    }


    //GET all document
    public List<DocumentListResponse>  getAllDocuments(Long diaryId){
        diaryService.getAccessibleDiary(diaryId);

        return documentRepository.findAllDocumentsByDiaryId(diaryId)
                .stream()
                .map(doc -> new DocumentListResponse(
                        doc.getDocumentId(),
                        doc.getDate()
                ))
                .toList();
    }

    //GET document by diary id and document id
    public Document getDocument(Long diaryId, Long documentId) {
        diaryService.getAccessibleDiary(diaryId);

        return documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId, documentId)
                .orElseThrow(
                        () -> new AppException( HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found")
                );
    }

    public DocumentResponse editDocument(Long diaryId, Long documentId, DocumentRequest req) throws IOException {
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId, documentId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found"));
        document.setDate(req.getDate());

        documentRepository.save(document);

        return new DocumentResponse(document.getDate());
    }


    //DELETE DOCUMENT
    public void deleteDocument(Long diaryId, Long documentId) {
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId,documentId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found"));

        documentRepository.delete(document);
    }

    //SAVE yjs
    public void saveYjsState(Long diaryId, Long documentId, byte[] state){
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId,documentId)
                .orElseThrow(() -> new AppException( HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found"));

        document.setYjsState(state);

        documentRepository.save(document);

    }
    //RETRIEVE yjs
    public byte[] getYjsState(Long diaryId, Long documentId){
        diaryService.getAccessibleDiary(diaryId);

        Document document = documentRepository.findDocumentByDiaryIdAndDocumentId(diaryId,documentId)
                .orElseThrow(() ->new AppException( HttpStatus.NOT_FOUND, "DOCUMENT_NOT_FOUND", "Document not found"));

        return document.getYjsState();
    }
    public void saveVersionSnapshot(Long documentId){
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new AppException(
                        HttpStatus.NOT_FOUND,
                        "DOCUMENT_NOT_FOUND",
                        "Document not found"
                ));

              byte[] state = doc.getYjsState();
              revisionService.createRevision(documentId, state);
    }

}