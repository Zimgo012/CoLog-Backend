package com.zimgo.colog.messages.services.subservices;



import com.zimgo.colog.document.Document;
import com.zimgo.colog.messages.dto.payloads.OperationPayload;
import com.zimgo.colog.messages.dto.payloads.PresencePayload;
import com.zimgo.colog.messages.dto.payloads.YjsPayload;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class YjsMessageService {

    private SimpMessagingTemplate messagingTemplate;

    public YjsMessageService(SimpMessagingTemplate messagingTemplate){
        this.messagingTemplate = messagingTemplate;
    }

    public void processYjsMessage(Long diaryId, Long documentId, YjsPayload payload){
        messagingTemplate.convertAndSend("/topic/diary/" + diaryId +"/documentId/" + documentId + "/yjs",payload);
    }


}
