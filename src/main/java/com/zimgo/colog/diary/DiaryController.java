package com.zimgo.colog.diary;


import com.zimgo.colog.messages.MessageRepository;
import com.zimgo.colog.messages.Messages;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserRepository;
import com.zimgo.colog.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RestController
@RequestMapping("/diary")
public class DiaryController {

    public DiaryService diaryService;

    public UserService userService;
    public SimpMessagingTemplate messagingTemplate;

    //temporary
    public MessageRepository messageRepository;


    public DiaryController(DiaryService diaryService, SimpMessagingTemplate messagingTemplate, UserService userService,
                           MessageRepository messageRepository) {
        this.diaryService = diaryService;
        this.userService =  userService;
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;

    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getDiary(id));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDiary() {
        return ResponseEntity.ok(diaryService.getAllDiary());
    }

    @PostMapping("/create")
    public ResponseEntity<?> addDiary(@RequestBody Diary diary) {
        return ResponseEntity.ok(diaryService.addDiary(diary));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id) {
        diaryService.deleteDiary(id);
        return ResponseEntity.ok().build();
    }

    //temporary
    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getDiaryMessages(@PathVariable Long id) {

        Diary diary = diaryService.getDiary(id);

        return ResponseEntity.ok(diary.getMessages());
    }
    // List of webscoket session
    @MessageMapping("/diary/session/{diaryId}")
    public void userMessage(@DestinationVariable Long diaryId,
                            @Payload Map<String, Object> req) {

        System.out.println(req);

        String content = (String) req.get("content");

        Map<String, Object> senderMap =
                (Map<String, Object>) req.get("sender");

        Long senderId =
                Long.valueOf(senderMap.get("id").toString());

        User user = userService.getUserById(senderId);

        Diary diary = diaryService.getDiary(diaryId);

        Messages message = new Messages();

        message.setSender(user);
        message.setDiaryId(diary);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());

        messageRepository.save(message);

        messagingTemplate.convertAndSend(
                "/topic/diary/" + diaryId,
                message
        );
    }
}
