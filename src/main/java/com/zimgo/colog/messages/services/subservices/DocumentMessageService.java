package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.document.DocumentService;
import com.zimgo.colog.messages.dto.payloads.DocumentPayload;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DocumentMessageService {

    private UserService userService;
    private DocumentService documentService;
    private SimpMessagingTemplate messagingTemplate;

    public DocumentMessageService(UserService userService, DocumentService documentService, SimpMessagingTemplate messagingTemplate) {
        this.userService = userService;
        this.documentService = documentService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * This method is called when a message with the 'DOCUMENT' type is received.
     * Used for collaborative editing in the diary room.
     * @param diaryId - the id of the diary
     * @param payload - the message dto from client
     */
    public void processDocumentMessage(Long diaryId, DocumentPayload payload) throws IOException {


        System.out.println("📥 DOCUMENT MESSAGE RECEIVED");

        System.out.println(payload.getContent());

        User sender = userService.getUserById(payload.getSenderId());

        // later, sender information and other metadata will be used as metadata for edits

        documentService.editDocument(diaryId,payload.getDocumentId(),payload.getContent());

//        Document doc = documentService.getDocument(req.getDocumentId());
        String content = getContent(diaryId, payload.getDocumentId());

        messagingTemplate.convertAndSend("/topic/diary/" + diaryId + "/document", content);
    }

    // ===============================================
    //Utility method to extract file content in a file
    private String getContent(Long diaryId, Long documentId) throws IOException {
        Path filePath = Paths.get("storage", "diaryId-" + diaryId)
                .resolve("document-" + documentId + ".txt");

        if (!Files.exists(filePath)) {
            return "";
        }

        return Files.readString(filePath);
    }

}
