package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.deltaLog.DeltaLog;
import com.zimgo.colog.deltaLog.DeltaLogService;
import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.document.DocumentService;
import com.zimgo.colog.messages.dto.payloads.OperationPayload;
import com.zimgo.colog.messages.dto.payloads.enums.OperationCommandType;
import com.zimgo.colog.operation.Operation;
import com.zimgo.colog.operation.OperationService;
import com.zimgo.colog.operation.OperationType;
import com.zimgo.colog.operation.dto.OperationDTO;
import com.zimgo.colog.operation.resolver.operationaltransform.OTResolver;
import jakarta.transaction.Transactional;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class CollaborationMessageService {

    private final DocumentSequencerService documentSequencerService;
    private final OTResolver oTResolver;
    private SimpMessagingTemplate messagingTemplate;
    private  DocumentService documentService;
    private  DiaryService diaryService;
    private  DeltaLogService deltaLogService;
    private  OperationService operationService;


    public CollaborationMessageService(SimpMessagingTemplate simpMessagingTemplate,
                                       DocumentService documentService,
                                       DiaryService diaryService,
                                       DeltaLogService deltaLogService,
                                       OperationService operationService,
                                       DocumentSequencerService documentSequencerService,
                                       OTResolver oTResolver) {
        this.messagingTemplate = simpMessagingTemplate;
        this.documentService = documentService;
        this.diaryService = diaryService;
        this.deltaLogService = deltaLogService;
        this.operationService = operationService;
        this.documentSequencerService = documentSequencerService;
        this.oTResolver = oTResolver;
    }

    // Main entry point
    @Transactional
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

        //Latest Log
        DeltaLog finalLog = recentLog;

        Operation operationEntity = convertOperationPayloadToEntity(doc, recentLog, payload );

        //1. Sequencer
        documentSequencerService.submit(doc.getDocumentId(), ()-> {

            // if update
            // extract delete
            //transform
            // extract insert
            //transform

            Operation transformed;

            if (operationEntity.getOperationType() == OperationType.UPDATE){

            }else{
                 transformed =  oTResolver.initOTResolver(operationEntity, doc);
            }



            //3. Apply operation to the document - using the pointer from deltalog
            try {
                deltaLogService.applyOperation(transformed,finalLog);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //4. Save document state
            documentService.incrementDocRevision(doc);
            finalLog.setEndRevision(doc.getCurrentRevision());
            deltaLogService.saveDeltalog(finalLog);
            transformed.setAppliedRevision(
                    doc.getCurrentRevision()
            );
            documentService.saveDocument(doc);
            //5. Save operation - Operation
            operationService.saveOperation(transformed);
            //6. Publish operations
            publish(diaryId, payload.getDocumentId(), doc.getCurrentRevision(),  transformed);
        });
    }

    private void publish(Long diaryId, Long documentId, Long docRevision, Operation op){
        OperationDTO dto = new OperationDTO();

        dto.setDocumentId(documentId);
        dto.setDeltaLogId(op.getDeltaLog().getDeltaLogId());
        dto.setOperationType(op.getOperationType().name());
        dto.setIndex(op.getIndex());
        dto.setText(op.getText());
        dto.setLength(op.getLength());
        dto.setTimestamp(op.getTimestamp());

        //publish revision number
        dto.setRevision(docRevision);


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
        operation.setOperationType(convertOperationType(payload.getOperation()));
        operation.setIndex(payload.getIndex());
        operation.setText(payload.getText());
        operation.setLength(payload.getLength());

        //set baserevision from frontend
        operation.setBaseRevision(payload.getBaseRevision());


        return operation;
    }

    private OperationType convertOperationType(
            OperationCommandType commandType
    ) {
        return OperationType.valueOf(commandType.name());
    }

}
