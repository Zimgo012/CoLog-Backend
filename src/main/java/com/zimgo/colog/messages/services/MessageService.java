package com.zimgo.colog.messages.services;

import com.zimgo.colog.messages.dto.MessageRequest;
import com.zimgo.colog.messages.dto.payloads.*;
import com.zimgo.colog.messages.services.subservices.*;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageService {

    public ChatMessageService chatMessageService;

    public DocumentMessageService documentMessageService;

    public FileMessageService fileMessageService;

    public NotificationMessageService notificationMessageService;

    public PresenceMessageService presenceMessageService;

    public YjsMessageService yjsMessageService;

    public MessageService(ChatMessageService chatMessageService, DocumentMessageService documentMessageService, FileMessageService fileMessageService, NotificationMessageService notificationMessageService, PresenceMessageService presenceMessageService, YjsMessageService yjsMessageService) {
        this.chatMessageService = chatMessageService;
        this.documentMessageService = documentMessageService;
        this.fileMessageService = fileMessageService;
        this.notificationMessageService = notificationMessageService;
        this.presenceMessageService = presenceMessageService;
        this.yjsMessageService = yjsMessageService;

    }

    public void processChatMessage(Long diaryId, ChatPayload payload){
        chatMessageService.processChatMessage(diaryId, payload);
    }

    public void processDocumentMessage(Long diaryId, DocumentPayload payload) throws IOException {
        documentMessageService.processDocumentMessage(diaryId, payload);
    }

    public void processPresenceMessage(Long diaryId, Long documentId, PresencePayload payload){
        presenceMessageService.processPresenceMessage(diaryId, documentId, payload);
    }
    public void processLeaveMessage(Long diaryId){
        notificationMessageService.processLeaveMessage(diaryId);
    }

    public void processJoinMessage(Long diaryId){
        notificationMessageService.processJoinMessage(diaryId);
    }

    public void processFileMessage(Long diaryId, MessageRequest fileName){
        fileMessageService.processFileMessage(diaryId, fileName);
    }

    public void processYjsUpdate(Long diaryId, Long documentId, YjsPayload yjsPayload){
        yjsMessageService.processYjsMessage(diaryId, documentId, yjsPayload);
    }

}
