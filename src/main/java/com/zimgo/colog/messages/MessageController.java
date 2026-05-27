package com.zimgo.colog.messages;

import com.zimgo.colog.messages.dto.MessageRequest;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;


@Controller
public class MessageController {

    public MessageService messageService;
    public SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService,
                             SimpMessagingTemplate messagingTemplate) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }


    @MessageMapping("/diary/session/{diaryId}")
    public void userMessage(@DestinationVariable Long diaryId,
                            @Payload MessageRequest req) throws IOException {

            if (req.getType() == null){
                throw new RuntimeException("Message type is null!");
            }

            switch (req.getType()) {

                case DOCUMENT -> messageService.processDocumentMessage(diaryId, req);
                case CHAT -> messageService.processChatMessage(diaryId,req);
                case LEAVE -> messageService.processLeaveMessage(diaryId);
                case JOIN -> messageService.processJoinMessage(diaryId);
                case FILE -> messageService.processFileMessage(diaryId, req);

                //handler if error here
                default -> System.out.println("Invalid message type");
            }

    }


}
