package com.zimgo.colog.diary;


import com.zimgo.colog.diary.dto.DiaryEditRequest;
import com.zimgo.colog.diary.dto.DiaryRequest;
import com.zimgo.colog.diary.dto.DiaryResponse;
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



    public DiaryController(DiaryService diaryService, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.diaryService = diaryService;
        this.userService =  userService;
        this.messagingTemplate = messagingTemplate;

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getDiary(id));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id) {

        return ResponseEntity.ok(diaryService.deleteDiary(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editDiary(@PathVariable Long id, @RequestBody DiaryEditRequest req){
        return ResponseEntity.ok(diaryService.editDiary(id,req));
    }

    @GetMapping("/my")
    public ResponseEntity<List<DiaryResponse>> getMyDiaries(){
        return ResponseEntity.ok(diaryService.getMyDiaries());
    }

}
