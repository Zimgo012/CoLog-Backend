package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.messages.MessageRepository;
import com.zimgo.colog.messages.Messages;
import com.zimgo.colog.messages.dto.MessageRespond;
import com.zimgo.colog.messages.dto.payloads.ChatPayload;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDateTime;

@Service
public class ChatMessageService {
    private MessageRepository messageRepository;
    private UserService userService;
    private DiaryService diaryService;
    private SimpMessagingTemplate messagingTemplate;

    public ChatMessageService(MessageRepository messageRepository,
                              UserService userService,
                              DiaryService diaryService,
                              SimpMessagingTemplate messagingTemplate) {

        this.messageRepository = messageRepository;
        this.userService = userService;
        this.diaryService = diaryService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Convert a message from a client to a message entity */
    private Messages convertToMessage(User sender, Diary diary, String content){

        Messages message = new Messages();
        message.setContent(content);
        message.setSender(sender);
        message.setDiary(diary);
        message.setTimestamp(LocalDateTime.now());

        return message;
    }

    /**
     * Convert a message to a message respond
     */
    private MessageRespond convertToResponse(User sender, String content){
        //id,content,timestamp,senderId,senderName

        MessageRespond res = new MessageRespond();
        res.setContent(content);
        res.setSenderId(sender.getUserId());
        res.setSenderName(sender.getFirstName() + " " + sender.getLastName());
        res.setTimestamp(LocalDateTime.now());

        return res;
    }
    public void processChatMessage(Long diaryId, ChatPayload payload, Long userId){

        User sender = userService.getUserById(userId);
        Diary diary = diaryService.getAccessibleDiary(diaryId, userId);

        if (diary == null){
            throw new RuntimeException("Diary does not exist!");
        }
        if (sender == null){
            throw new RuntimeException("User does not exist!");
        }

        String content = payload.getContent();


        Messages message = convertToMessage(sender, diary, content);
        messageRepository.save(message);

        MessageRespond res = convertToResponse(sender, content);

        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/chat", res);

    }
}
