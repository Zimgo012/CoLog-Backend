package com.zimgo.colog.messages;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.exception.AppException;
import com.zimgo.colog.messages.dto.MessageRequest;
import com.zimgo.colog.messages.dto.payloads.ChatPayload;
import com.zimgo.colog.messages.dto.payloads.PresencePayload;
import com.zimgo.colog.messages.dto.payloads.YjsPayload;
import com.zimgo.colog.messages.services.MessageService;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import tools.jackson.databind.ObjectMapper;

import java.security.Principal;
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
                            SimpMessageHeaderAccessor accessor,
                            Principal principal) {
        requireBinding(diaryId, accessor);

        if (!(principal instanceof Authentication authentication) || !authentication.isAuthenticated()) {
            throw new AppException(
                    HttpStatus.UNAUTHORIZED,
                    "UNAUTHENTICATED_USER",
                    "User must be authenticated to send messages"
            );
        }

        if (!(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
            throw new AppException(
                    HttpStatus.UNAUTHORIZED,
                    "INVALID_AUTHENTICATION",
                    "WebSocket authentication is invalid"
            );
        }

        Long userId = userDetails.getId();

        if (req == null || req.getType() == null) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "MESSAGE_TYPE_REQUIRED",
                    "Message type is required"
            );
        }

        switch (req.getType()) {

            case PRESENCE -> {
                PresencePayload payload = convertPayload(req.getPayload(), PresencePayload.class);

                messageService.processPresenceMessage(
                        diaryId,
                        payload.getDocumentId(),
                        payload
                );
            }

            //CRDT update using y.js
            case YJSUPDATE->{
                YjsPayload payload = convertPayload(req.getPayload(), YjsPayload.class);
                messageService.processYjsUpdate(diaryId, payload.getDocumentId(), payload);
            }

            case CHAT -> {
                ChatPayload payload = convertPayload(req.getPayload(), ChatPayload.class);
                messageService.processChatMessage(diaryId, payload, userId);
            }
            case LEAVE -> messageService.processLeaveMessage(diaryId);
            case JOIN -> messageService.processJoinMessage(diaryId);
            case FILE -> messageService.processFileMessage(diaryId, req);

            default -> throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_MESSAGE_TYPE",
                    "Unsupported message type"
            );
        }

    }

    private void requireBinding(Long diaryId, SimpMessageHeaderAccessor accessor) {
        Map<String, Object> accessorAttributes = accessor.getSessionAttributes();

        if (accessorAttributes == null){
            throw new AppException(
                    HttpStatus.UNAUTHORIZED,
                    "WEBSOCKET_SESSION_NOT_FOUND",
                    "WebSocket session attributes are missing"
            );
        }

        Long boundDiaryId = (Long) accessorAttributes.get("DIARY_ID");

        if (boundDiaryId == null) {
            throw new AppException(
                    HttpStatus.FORBIDDEN,
                    "DIARY_SESSION_NOT_BOUND",
                    "No diary is bound to this WebSocket session"
            );
        }

        if(!boundDiaryId.equals(diaryId)){
            throw new AppException(
                    HttpStatus.FORBIDDEN,
                    "DIARY_SESSION_MISMATCH",
                    "This WebSocket session is not authorized for the requested diary"
            );
        }
    }

    private <T> T convertPayload(Object payload, Class<T> payloadType) {
        if (payload == null) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "MESSAGE_PAYLOAD_REQUIRED",
                    "Message payload is required"
            );
        }

        try {
            return objectMapper.convertValue(payload, payloadType);
        } catch (IllegalArgumentException ex) {
            throw new AppException(
                    HttpStatus.BAD_REQUEST,
                    "INVALID_MESSAGE_PAYLOAD",
                    "Message payload has an invalid format"
            );
        }
    }


}
