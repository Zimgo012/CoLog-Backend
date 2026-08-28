package com.zimgo.colog.messages;

import com.zimgo.colog.messages.dto.payloads.*;
import com.zimgo.colog.messages.dto.MessageRequest;
import com.zimgo.colog.messages.services.MessageService;
import com.zimgo.colog.messages.services.subservices.YjsMessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Map;


@Controller
public class MessageController {

    private final ObjectMapper objectMapper;
    public MessageService messageService;
    public SimpMessagingTemplate messagingTemplate;

    public MessageController(MessageService messageService,
                             SimpMessagingTemplate messagingTemplate,
                             ObjectMapper objectMapper) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.objectMapper = objectMapper;
    }


    @MessageMapping("/diary/session/{diaryId}")
    public void userMessage(@DestinationVariable Long diaryId,
                            @Payload MessageRequest req,
                            SimpMessageHeaderAccessor accessor
                            ) throws IOException {


        requireBinding(diaryId, accessor);

        if (req.getType() == null) {
            throw new RuntimeException("Message type is null!");
        }

        System.out.println(req.getType());

        switch (req.getType()) {

            case PRESENCE -> {
                PresencePayload payload = objectMapper.convertValue(req.getPayload(), PresencePayload.class);

                messageService.processPresenceMessage(
                        diaryId,
                        payload.getDocumentId(),
                        payload
                );
            }

            //CRDT update using y.js
            case YJSUPDATE->{
                YjsPayload payload = objectMapper.convertValue(req.getPayload(), YjsPayload.class);
                messageService.processYjsUpdate(diaryId, payload.getDocumentId(), payload);
            }

            case CHAT -> {
                ChatPayload payload = objectMapper.convertValue(req.getPayload(), ChatPayload.class);
                messageService.processChatMessage(diaryId, payload);
            }
            case LEAVE -> messageService.processLeaveMessage(diaryId);
            case JOIN -> messageService.processJoinMessage(diaryId);
            case FILE -> messageService.processFileMessage(diaryId, req);

            //handler if error here
            default -> System.out.println("Invalid message type");
        }

    }

    private void requireBinding(Long diaryId, SimpMessageHeaderAccessor accessor) throws AccessDeniedException {
        Map<String, Object> accessorAttributes = accessor.getSessionAttributes();

        if (accessorAttributes == null){
            throw new AccessDeniedException("No websocket session");
        }
        System.out.println("SESSION ATTRIBUTES = " + accessorAttributes);

        Long boundDiaryId = (Long) accessorAttributes.get("DIARY_ID");

        if (boundDiaryId == null) {
            throw new AccessDeniedException("No diary bounded for session");
        }
        System.out.println(
                "BOUND DIARY = " +
                        accessorAttributes.get("DIARY_ID")
        );

        if(!boundDiaryId.equals(diaryId)){
            throw new AccessDeniedException("Bounded diary dont matched");
        }
    }


}
