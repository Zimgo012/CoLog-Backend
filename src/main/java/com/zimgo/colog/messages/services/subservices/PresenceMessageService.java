package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.messages.MessageRepository;
import com.zimgo.colog.messages.dto.payloads.PresencePayload;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class PresenceMessageService {

    private MessageRepository messageRepository;
    private UserService userService;
    private DiaryService diaryService;
    private SimpMessagingTemplate messagingTemplate;

    public PresenceMessageService(MessageRepository messageRepository,
                              UserService userService,
                              DiaryService diaryService,
                              SimpMessagingTemplate messagingTemplate) {

        this.messageRepository = messageRepository;
        this.userService = userService;
        this.diaryService = diaryService;
        this.messagingTemplate = messagingTemplate;
    }
    public void processPresenceMessage(Long diaryId, Long documentId, PresencePayload payload){
        messagingTemplate.convertAndSend("/topic/diary/" + diaryId +"/documentId/" + documentId + "/com/zimgo/colog/presence",payload);
    }
}
