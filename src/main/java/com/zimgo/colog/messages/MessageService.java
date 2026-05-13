package com.zimgo.colog.messages;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.messages.dto.MessageRequest;
import com.zimgo.colog.messages.dto.MessageRespond;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
public class MessageService {

    public MessageRepository messageRepository;
    public SimpMessagingTemplate messagingTemplate;
    public UserService userService;
    public DiaryService diaryService;


    public MessageService(MessageRepository messageRepository,
                          SimpMessagingTemplate messagingTemplate,
                          UserService userService,
                          DiaryService diaryService) {

        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.diaryService = diaryService;
    }

    /**
     * Convert a message from a client to a message entity */
    private Messages convertToMessage( User sender, Diary diary, String content){

        Messages message = new Messages();
        message.setContent(content);
        message.setSender(sender);
        message.setDiaryId(diary);
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
        res.setSenderId(sender.getId());
        res.setSenderName(sender.getFirstName() + " " + sender.getLastName());
        res.setTimestamp(LocalDateTime.now());

        return res;
    }

    /**
     * This method is called when a message with the 'CHAT' type is received.
     * Used for chatting in the diary room.
     * @param diaryId - the id of the diary
     * @param req - the message dto from client
     */
    public void processChatMessage(Long diaryId, MessageRequest req){

        User sender = userService.getUserById(req.getSenderId());
        Diary diary = diaryService.getDiary(diaryId);

        if (diary == null){
            throw new RuntimeException("Diary does not exist!");
        }
        if (sender == null){
            throw new RuntimeException("User does not exist!");
        }

        String content = req.getContent();


        Messages message = convertToMessage(sender, diary, content);
        messageRepository.save(message);

        MessageRespond res = convertToResponse(sender, content);

        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/chat", res);

    }

    /**
     * This method is called when a message with the 'DOCUMENT' type is received.
     * Used for collaborative editing in the diary room.
     * @param diaryId - the id of the diary
     * @param req - the message dto from client
     */
    public void processDocumentMessage(Long diaryId, MessageRequest req){

        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/document");
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

    /**
     * This method is called when a message with the 'FILE' type is received.
     * This embeds a file inside the diary. Can only have two files per diary. Once per client
     * @param diaryId - the id of the diary
     * @param req - the message dto from client
     */
    public void processFileMessage(Long diaryId, MessageRequest req){
        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/file");
    }


}
