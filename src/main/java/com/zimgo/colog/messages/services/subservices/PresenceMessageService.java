package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.chat.ChatRepository;
import com.zimgo.colog.messages.dto.payloads.PresencePayload;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PresenceMessageService {

    private SimpMessagingTemplate messagingTemplate;

    public PresenceMessageService(SimpMessagingTemplate messagingTemplate) {


        this.messagingTemplate = messagingTemplate;
    }
    public void processPresenceMessage(Long diaryId, Long documentId, PresencePayload payload){
        messagingTemplate.convertAndSend("/topic/diary/" + diaryId +"/documentId/" + documentId + "/presence",payload);
    }
}
