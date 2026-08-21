package com.zimgo.colog.diary;


import com.zimgo.colog.diary.dto.DiaryRequest;
import com.zimgo.colog.messages.MessageRepository;
import com.zimgo.colog.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return ResponseEntity.ok(diaryService.getAccessibleDiary(id));
    }

    @GetMapping("/collaborated")
    public ResponseEntity<?> getAllCollaboratedDiaries()
    {
        return ResponseEntity.ok(diaryService.getAllCollaboratedDiaries());
    }

    @PostMapping("/create")
    public ResponseEntity<?> addDiary(@RequestBody DiaryRequest req) {
        return ResponseEntity.ok(diaryService.addDiary(req));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id) {
        diaryService.deleteDiary(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<Diary>> getMyDiaries(){
        return ResponseEntity.ok(diaryService.getMyDiaries());
    }

}
