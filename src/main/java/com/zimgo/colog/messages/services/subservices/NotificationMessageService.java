package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.chat.ChatRepository;
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

}
