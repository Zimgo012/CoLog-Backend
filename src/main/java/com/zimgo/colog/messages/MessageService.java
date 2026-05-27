package com.zimgo.colog.messages;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.messages.dto.MessageRequest;
import com.zimgo.colog.messages.dto.MessageRespond;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.document.DocumentService;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class MessageService {

    public MessageRepository messageRepository;
    public SimpMessagingTemplate messagingTemplate;
    public UserService userService;
    public DiaryService diaryService;

    public DocumentService documentService;


    public MessageService(MessageRepository messageRepository,
                          SimpMessagingTemplate messagingTemplate,
                          UserService userService,
                          DiaryService diaryService,
                          DocumentService documentService) {

        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
        this.diaryService = diaryService;
        this.documentService = documentService;
    }

    /**
     * Convert a message from a client to a message entity */
    private Messages convertToMessage( User sender, Diary diary, String content){

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
    public void processDocumentMessage(Long diaryId, MessageRequest req) throws IOException {


        System.out.println("📥 DOCUMENT MESSAGE RECEIVED");

        System.out.println(req.getContent());

        User sender = userService.getUserById(req.getSenderId());

        // later, sender information and other metadata will be used as metadata for edits

        documentService.editDocument(diaryId,req.getDocumentId(),req.getContent());

//        Document doc = documentService.getDocument(req.getDocumentId());
        String content = getContent(diaryId, req.getDocumentId());

        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/document", content);
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

    //Ultility method to extract file content in a file
    private String getContent(Long diaryId, Long documentId) throws IOException {
        Path filePath = Paths.get("storage", "diaryId-" + diaryId)
                .resolve("document-" + documentId + ".txt");

        if (!Files.exists(filePath)) {
            return "";
        }

        return Files.readString(filePath);
    }

}
