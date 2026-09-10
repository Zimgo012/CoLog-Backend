package com.zimgo.colog.diary;


import com.zimgo.colog.diary.dto.*;
import com.zimgo.colog.messages.dto.payloads.CollaboratorPayload;
import com.zimgo.colog.messages.dto.payloads.enums.CollaboratorOperationType;
import com.zimgo.colog.messages.services.MessageService;
import com.zimgo.colog.user.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.zimgo.colog.exception.RequestValidator.requireBody;

@RestController
@RequestMapping("/diary")
public class DiaryController {

    public DiaryService diaryService;

    public UserService userService;

    private final MessageService messageService;


    //temporary



    public DiaryController(DiaryService diaryService, MessageService messageService, UserService userService) {
        this.diaryService = diaryService;
        this.userService =  userService;
        this.messageService = messageService;

    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDiary(@PathVariable Long id) {
        return ResponseEntity.ok(diaryService.getDiary(id));
    }
    @GetMapping("/{id}/collaborators/")
    public ResponseEntity<?> getCollaborators(@PathVariable Long id){
        return ResponseEntity.ok(diaryService.getAllCollaborators(id));
    }


    @GetMapping("/collaborated")
    public ResponseEntity<?> getAllCollaboratedDiaries()
    {
        return ResponseEntity.ok(diaryService.getAllCollaboratedDiaries());
    }

    @PostMapping("/{diaryId}/add/collaborator")
    public ResponseEntity<?> inviteUser(@PathVariable Long diaryId, @RequestBody DiaryCollaboratorRequest req){


        DiaryCollaboratorResponse resp = diaryService.addCollaborator(diaryId, requireBody(req));

        CollaboratorPayload payload = new CollaboratorPayload(
                resp.getDiaryId(),
                resp.getTitle(),
                resp.getDiayOwnerName(),
                resp.getName(),
                resp.getEmail(),
                resp.getUsername(),
                CollaboratorOperationType.ADD_COLLABORATOR);


        messageService.processNotification(resp.getUsername(),payload);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{diaryId}/remove/collaborator")
    public ResponseEntity<?> removeUser(@PathVariable Long diaryId, @RequestBody DiaryCollaboratorRequest req){

        DiaryCollaboratorResponse resp = diaryService.removeCollaborator(diaryId, requireBody(req));

        CollaboratorPayload payload = new CollaboratorPayload(
                resp.getDiaryId(),
                resp.getTitle(),
                resp.getDiayOwnerName(),
                resp.getName(),
                resp.getEmail(),
                resp.getUsername(),
                CollaboratorOperationType.REMOVE_COLLABORATOR);

        messageService.processNotification(resp.getUsername(),payload);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/create")
    public ResponseEntity<?> addDiary(@RequestBody DiaryRequest req) {
        return ResponseEntity.ok(diaryService.addDiary(requireBody(req)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long id) {

        return ResponseEntity.ok(diaryService.deleteDiary(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> editDiary(@PathVariable Long id, @RequestBody DiaryEditRequest req){
        return ResponseEntity.ok(diaryService.editDiary(id, requireBody(req)));
    }

    @GetMapping("/my")
    public ResponseEntity<List<DiaryResponse>> getMyDiaries(){
        return ResponseEntity.ok(diaryService.getMyDiaries());
    }

}
