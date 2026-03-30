package com.zimgo.colog.diary;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RestController
@RequestMapping("/diary")
public class DiaryController {

    public DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getDiary(id));
    }

    @PostMapping("/all")
    public ResponseEntity<?> addDiary(@RequestBody Diary diary) {
        return ResponseEntity.ok(diaryService.addDiary(diary));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id) {
        diaryService.getDiary(id);
        return ResponseEntity.ok().build();
    }
}
