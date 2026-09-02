package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.chat.ChatRepository;
import com.zimgo.colog.chat.Chat;
import com.zimgo.colog.messages.dto.MessageRespond;
import com.zimgo.colog.messages.dto.payloads.ChatPayload;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatMessageService {
    private ChatRepository chatRepository;
    private UserService userService;
    private DiaryService diaryService;
    private SimpMessagingTemplate messagingTemplate;

    public ChatMessageService(ChatRepository chatRepository,
                              UserService userService,
                              DiaryService diaryService,
                              SimpMessagingTemplate messagingTemplate) {

        this.chatRepository = chatRepository;
        this.userService = userService;
        this.diaryService = diaryService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Convert a message from a client to a message entity */
    private Chat convertToMessage(User sender, Diary diary, String content){

        Chat message = new Chat();
        message.setContent(content);
        message.setSender(sender);
        message.setDiary(diary);
        message.setTimestamp(LocalDateTime.now());

        return message;
    }

    /**
     * Convert a message to a message respond
     */
    private MessageRespond convertToResponse(Chat messageSaved){
        //id,content,timestamp,senderId,senderName

        MessageRespond res = new MessageRespond();
        res.setContent(messageSaved.getContent());
        res.setSenderId(messageSaved.getSender().getUserId());
        res.setSenderName(messageSaved.getSender().getFirstName() + " " + messageSaved.getSender().getLastName());
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


        Chat message = convertToMessage(sender, diary, content);
        Chat savedMessage = chatRepository.save(message);

        MessageRespond res = convertToResponse(savedMessage);

        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/chat", res);

    }
}
