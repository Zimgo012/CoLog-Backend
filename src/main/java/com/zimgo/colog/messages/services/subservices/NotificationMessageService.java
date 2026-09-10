package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.chat.ChatRepository;
import com.zimgo.colog.diary.dto.DiaryCollaboratorResponse;
import com.zimgo.colog.messages.dto.payloads.CollaboratorPayload;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationMessageService {


    private SimpMessagingTemplate messagingTemplate;

    public NotificationMessageService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    /**
     *  Leave and join message - will notify the diary room  */
    public void processLeaveMessage(Long diaryId){
        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/leave");
    }

    /**
     *  Leave and join message - will notify the diary room  */
    public void processJoinMessage(Long diaryId){
        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/join");
    }

    public void processNotification(String username, CollaboratorPayload payload){
        messagingTemplate.convertAndSendToUser(username,"/queue/notification", payload );
    }

}
