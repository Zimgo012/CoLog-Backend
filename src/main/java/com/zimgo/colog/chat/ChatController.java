package com.zimgo.colog.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService  chatService;

    public ChatController(ChatService chatService){
        this.chatService = chatService;
    }
    @GetMapping("/{diaryId}")
    public ResponseEntity<?> getChatHistory(@PathVariable Long diaryId){
        return ResponseEntity.ok(chatService.getChatHistory(diaryId));
    }
}
