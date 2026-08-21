package com.zimgo.colog.diary;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.diary.dto.DiaryRequest;
import com.zimgo.colog.diary.dto.DiaryResponse;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiaryService {
    private final UserService userService;
    public DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository, UserService userService) {
        this.diaryRepository = diaryRepository;
        this.userService = userService;
    }

    public Diary getDiary(Long diaryId) {
        if (!diaryRepository.existsById(diaryId)) {
            throw new RuntimeException("Diary does not exist!");


        }
        return diaryRepository.findById(diaryId).get();
    }
    //GET diary thats only accesible with diary ID
    public Diary getAccessibleDiary(Long diaryId) {

        Long userId = getIdFromJwt();

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() ->
                        new RuntimeException("Diary does not exist")
                );


        if(!canAccessDiary(diary,userId)){
            throw new AccessDeniedException("User not authorized");
        }

        return diary;
    }


    //GET diary by owner (using jwt)
    public  List<Diary> getMyDiaries(){

       Long userId = getIdFromJwt();
       return diaryRepository.findAllByOwnerUserId(userId);
    }

    //GET all collaborated diaries of owner
    public List<Diary> getAllCollaboratedDiaries(){

        Long userId = getIdFromJwt();
        return diaryRepository.findAllByCollaboratorsUserId(userId);
    }


    //CREATE diary
    public DiaryResponse addDiary(DiaryRequest req) {

        //get ID from jwt and find User
        Long id = getIdFromJwt();
        User user = userService.getUserById(id);

        Diary diary = new Diary();

        diary.setTitle(req.getTitle());
        diary.setCreatedAt(LocalDate.now());
        diary.setLastOpenedAt(LocalDate.now());
        diary.setOwner(user);
        diary.setPublic(false);

        diaryRepository.save(diary);

        return new DiaryResponse(diary.getTitle(),diary.isPublic(),diary.getCreatedAt());
    }

    //ADD collaborators to the diary
    public void addCollaborator(Long diaryId, Long userId) {

        Diary diary = getOwnedDiary(diaryId);

        User user = userService.getUserById(userId);

        if (!diary.getCollaborators().contains(user)) {
            diary.getCollaborators().add(user);
        }

        diaryRepository.save(diary);
    }

    //REMOVE a collaborator in diary
    public void removeCollaborator(Long diaryId, Long userId) {

        Diary diary = getOwnedDiary(diaryId);

        User user = userService.getUserById(userId);

        diary.getCollaborators().remove(user);

        diaryRepository.save(diary);
    }

    //DELETE diary
    public void deleteDiary(Long diaryId) {

        Diary diary = getOwnedDiary(diaryId);

        diaryRepository.delete(diary);
    }

    private boolean canAccessDiary(Diary diary, Long userId) {

        boolean isOwner =
                diary.getOwner()
                        .getUserId()
                        .equals(userId);

        boolean isCollaborator =
                diary.getCollaborators()
                        .stream()
                        .anyMatch(user ->
                                user.getUserId().equals(userId)
                        );

        return isOwner || isCollaborator;
    }


    // HELPER
    private Long getIdFromJwt(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            throw new AccessDeniedException(
                    "User is not authenticated"
            );
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return  userDetails.getId();
    }

    // GET a specific diary, but only if current user owns it
    private Diary getOwnedDiary(Long diaryId) {

        Long userId = getIdFromJwt();

        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() ->
                        new RuntimeException("Diary does not exist")
                );

        if (!diary.getOwner().getUserId().equals(userId)) {
            throw new AccessDeniedException(
                    "Only the diary owner can perform this action"
            );
        }

        return diary;
    }



}
