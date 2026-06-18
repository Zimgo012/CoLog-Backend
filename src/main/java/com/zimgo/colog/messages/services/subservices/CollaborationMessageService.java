package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.deltaLog.DeltaLog;
import com.zimgo.colog.deltaLog.DeltaLogService;
import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.document.DocumentService;
import com.zimgo.colog.messages.dto.payloads.OperationPayload;
import com.zimgo.colog.operation.Operation;
import com.zimgo.colog.operation.OperationService;
import com.zimgo.colog.operation.dto.OperationDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CollaborationMessageService {

    private SimpMessagingTemplate messagingTemplate;
    private  DocumentService documentService;
    private  DiaryService diaryService;
    private  DeltaLogService deltaLogService;
    private  OperationService operationService;


    public CollaborationMessageService(SimpMessagingTemplate simpMessagingTemplate,
                                       DocumentService documentService,
                                       DiaryService diaryService,
                                       DeltaLogService deltaLogService,
                                       OperationService operationService) {
        this.messagingTemplate = simpMessagingTemplate;
        this.documentService = documentService;
        this.diaryService = diaryService;
        this.deltaLogService = deltaLogService;
        this.operationService = operationService;
    }

    // Main entry point
    public void processCollaborationMessage(Long diaryId, OperationPayload payload) throws IOException {

        Diary diary = diaryService.getDiary(diaryId);
        Document doc = documentService.getDocument(payload.getDocumentId());
        DeltaLog recentLog = deltaLogService.findTopLogByDocumentId(doc.getDocumentId());

        if (recentLog == null){
            recentLog = deltaLogService.createInitialLog(doc);
        }

        if (deltaLogService.shouldRotateLog(recentLog)){
            recentLog = deltaLogService.rotateLog(recentLog);
        }

//        recentLog = deltaLogService.rotateLog(recentLog);

        Operation operationEntity = convertOperationPayloadToEntity(doc, recentLog, payload );


        //1. Sequencer
        //2. Apply operation to the document - using the pointer from deltalog
        deltaLogService.applyOperation(operationEntity,recentLog);
        //3. Save document state
        documentService.saveDocument(doc);
        //4. Save operation - Operation
        operationService.saveOperation(operationEntity);

        //6. Publish operations
        publish(diaryId, payload.getDocumentId(), operationEntity);

    }

    private void publish(Long diaryId, Long documentId, Operation op){
        OperationDTO dto = new OperationDTO();

        dto.setDocumentId(documentId);
        dto.setDeltaLogId(op.getDeltaLog().getDeltaLogId());
        dto.setOperationType(op.getOperationType().name());
        dto.setIndex(op.getIndex());
        dto.setText(op.getText());
        dto.setLength(op.getLength());
        dto.setTimestamp(op.getTimestamp());

        messagingTemplate.convertAndSend(
                "/topic/diary/" + diaryId + "/documentId/" + documentId + "/collaboration",
                dto
        );
    }



    private Operation convertOperationPayloadToEntity(Document doc, DeltaLog deltaLog, OperationPayload payload) {
        // Convert payload
        Operation operation = new Operation();

        operation.setDocument(doc);


        //Added the operation to log
        operation.setDeltaLog(deltaLog);

        operation.setTimestamp(LocalDateTime.now());
        operation.setOperationType(payload.getOperation());
        operation.setIndex(payload.getIndex());
        operation.setText(payload.getText());
        operation.setLength(payload.getLength());

        return operation;
    }

}
