package com.zimgo.colog.diary;

import com.zimgo.colog.auth.security.CustomUserDetails;
import com.zimgo.colog.diary.dto.*;
import com.zimgo.colog.messages.dto.payloads.CollaboratorPayload;
import com.zimgo.colog.messages.dto.payloads.enums.CollaboratorOperationType;
import com.zimgo.colog.messages.services.MessageService;
import com.zimgo.colog.messages.services.subservices.NotificationMessageService;
import com.zimgo.colog.user.User;
import com.zimgo.colog.user.UserService;
import org.aspectj.weaver.ast.Not;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing and interacting with diaries.
 *
 * This class provides functionality for retrieving, creating, updating, and deleting diaries,
 * as well as managing collaborators. It also checks user permissions to ensure secure access.
 */
@Service
public class DiaryService {
    private final UserService userService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    public DiaryRepository diaryRepository;

    public DiaryService(DiaryRepository diaryRepository,
                        UserService userService,
                        SimpMessagingTemplate simpMessagingTemplate) {
        this.diaryRepository = diaryRepository;
        this.userService = userService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    /**
     * Retrieves a diary by its unique identifier.
     *
     * @param diaryId The unique identifier of the diary to retrieve.
     * @return The {@link Diary} object corresponding to the provided diaryId.
     * @throws RuntimeException If the diary with the given identifier does not exist.
     */
    public DiaryResponse getDiary(Long diaryId) {
        Diary diary = getAccessibleDiary(diaryId);

        DiaryResponse response = new DiaryResponse();
        response.setId(diary.getDiaryId());
        response.setCreatedAt(diary.getCreatedAt());
        response.setTitle(diary.getTitle());
        response.setColor(diary.getColor());
        response.setEmoji(diary.getEmoji());

        return response;
    }

    /**
     * Retrieves a diary that the current user has access to. Used in HTTP Request
     *
     * This method checks if the diary exists and whether the current user is authorized
     * to access it either as the owner or as a collaborator. If the user is not authorized
     * or if the diary does not exist, appropriate exceptions are thrown.
     *
     * @param diaryId The unique identifier of the diary to retrieve.
     * @return The {@link Diary} object corresponding to the provided diaryId.
     * @throws RuntimeException If the diary with the given identifier does not exist.
     * @throws AccessDeniedException If the current user is not authorized to access the diary.
     */
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

    /**
     * Retrieves a diary by its ID if the user has access to it. Used in WebSocket
     *
     * @param diaryId the ID of the diary to retrieve
     * @param userId the ID of the user attempting to access the diary
     * @return the requested diary if the user is authorized
     * @throws RuntimeException if the diary does not exist
     * @throws AccessDeniedException if the user is not authorized to access the diary
     */
    @Transactional(readOnly = true)
    public Diary getAccessibleDiary(Long diaryId, Long userId) {


        Diary diary = diaryRepository.findById(diaryId)
                .orElseThrow(() -> {

                    return new RuntimeException(
                            "Diary does not exist"
                    );
                });



        boolean access =
                canAccessDiary(
                        diary,
                        userId
                );


        if (!access) {

            throw new AccessDeniedException(
                    "User not authorized"
            );
        }

        return diary;
    }

    /**
     * Retrieves a list of diaries owned by the currently authenticated user.
     *
     * This method identifies the authenticated user via their JWT, fetches their user ID,
     * and retrieves all diaries associated with that user as the owner.
     *
     * @return A list of {@link Diary} objects representing the diaries owned by the current user.
     */
    public  List<DiaryResponse> getMyDiaries(){

       Long userId = getIdFromJwt();
       return diaryRepository.findAllByOwnerUserId(userId)
                .stream()
                .map(diary -> new DiaryResponse(
                        diary.getDiaryId(),
                        diary.getTitle(),
                        diary.getCreatedAt(),
                        diary.getOwner().getUsername(),
                        diary.getEmoji(),
                        diary.getColor()
                ))
                .toList();
    }

    /**
     * Retrieves a list of diaries where the currently authenticated user is a collaborator.
     *
     * This method identifies the authenticated user via their JWT,
     * fetches their user ID, and retrieves all diaries associated
     * with that user as a collaborator.
     *
     * @return A list of {@link Diary} objects representing the diaries
     *         the current user is collaborating on.
     */
    public List<DiaryCollaboratorsListResponse> getAllCollaborators(Long diaryId){

        return diaryRepository.findCollaboratorsExceptOwner(diaryId)
                .stream()
                .map(user -> new DiaryCollaboratorsListResponse(
                        user.getUserId(),
                        user.getUsername(),
                        user.getFirstName() + " " + user.getLastName(),
                        user.getEmail()
                )).toList();
    }

    /**
     * Retrieves a list of diaries collaborated on by the currently authenticated user.
     *
     * @return a list of DiaryResponse objects, each representing a diary the user is a collaborator on.
     */
    public List<DiaryResponse> getAllCollaboratedDiaries(){

        Long userId = getIdFromJwt();


        return diaryRepository.findAllByCollaboratorsUserId(userId)
                .stream()
                .map(diary -> new DiaryResponse(
                        diary.getDiaryId(),
                        diary.getTitle(),
                        diary.getCreatedAt(),
                        diary.getOwner().getUsername(),
                        diary.getColor(),
                        diary.getEmoji()
                ))
                .toList();
    }


    /**
     * Creates a new diary for the currently authenticated user based on the provided request.
     * The diary is initialized with the title provided in the {@code DiaryRequest},
     * the current date as the creation and last opened date, and is set to private by default.
     *
     * @param req The {@link DiaryRequest} containing the details for the new diary, such as its title.
     * @return A {@link DiaryResponse} containing the title, privacy status, and creation date of the newly created diary.
     */
    public DiaryResponse addDiary(DiaryRequest req) {

        //get ID from jwt and find User
        Long id = getIdFromJwt();
        User user = userService.getUserById(id);

        Diary diary = new Diary();

        diary.setTitle(req.getTitle());
        diary.setCreatedAt(LocalDate.now());
        diary.setLastOpenedAt(LocalDate.now());
        diary.setOwner(user);

        diaryRepository.save(diary);

        DiaryResponse response = new DiaryResponse();
        response.setId(diary.getDiaryId());
        response.setCreatedAt(diary.getCreatedAt());
        response.setTitle(diary.getTitle());
        response.setColor(diary.getColor());
        response.setEmoji(diary.getEmoji());

        return response;
    }

    /**
     * Adds a collaborator to a diary.
     *
     * This method allows the owner of a diary to add another user as a collaborator.
     * If the specified user is already a collaborator, no changes are made.
     *
     * @param diaryId The unique identifier of the diary to which the collaborator is being added.
     * @param req Request body of the function which includes email.
     * @throws RuntimeException If the diary does not exist or if the current user does not own the diary.
     */
    public DiaryCollaboratorResponse addCollaborator(Long diaryId, DiaryCollaboratorRequest req) {

        Diary diary = getOwnedDiary(diaryId);

        User user = userService.getUserByEmail(req.getEmail());

        if (!diary.getCollaborators().contains(user)) {
            diary.getCollaborators().add(user);
        }

        diaryRepository.save(diary);

        Long currentDiaryId = diary.getDiaryId();
        String diaryTitle = diary.getTitle();
        String diaryOwnerName = diary.getOwner().getUsername();
        Long id = user.getUserId();
        String name =  user.getFirstName() + user.getLastName();
        String email = user.getEmail();
        String username = user.getUsername();

        DiaryCollaboratorResponse res = new DiaryCollaboratorResponse(currentDiaryId,diaryTitle,diaryOwnerName,id,name,email,username);

        return res;
    }

    /**
     * Removes a collaborator from a diary.
     *
     * This method allows the owner of a diary to remove a specified user from the list of collaborators
     * associated with the diary. If the user is not currently a collaborator, no changes are made.
     *
     * @param diaryId The unique identifier of the diary from which the collaborator is being removed.
     * @param req Request body of the function which includes email.
     * @throws RuntimeException If the diary does not exist or if the current user does not own the diary.
     */
    public DiaryCollaboratorResponse removeCollaborator(Long diaryId, DiaryCollaboratorRequest req) {

        Diary diary = getOwnedDiary(diaryId);

        User user = userService.getUserByEmail(req.getEmail());
        boolean removed = diary.getCollaborators().remove(user);

        System.out.println("Removed: " + removed);
        System.out.println("Remaining collaborators: " + diary.getCollaborators().size());

        diaryRepository.save(diary);

        Long currentDiaryId = diary.getDiaryId();
        String diaryTitle = diary.getTitle();
        String diaryOwnerName = diary.getOwner().getUsername();
        Long id = user.getUserId();
        String name =  user.getFirstName() + user.getLastName();
        String email = user.getEmail();
        String username = user.getUsername();

        DiaryCollaboratorResponse res = new DiaryCollaboratorResponse(currentDiaryId,diaryTitle,diaryOwnerName,id,name,email,username);

        return res;
    }

    /**
     * Deletes a diary identified by its ID if it is owned by the current user.
     *
     * @param diaryId the unique identifier of the diary to be deleted
     */
    public DiaryDeleteResponse deleteDiary(Long diaryId) {

        Diary diary = getOwnedDiary(diaryId);
        Long id = diary.getDiaryId();
        String title  = diary.getTitle();

        diaryRepository.delete(diary);

        return new DiaryDeleteResponse(id, title);
    }

    /**
     * Edits an existing diary by updating its title, emoji, and color based on the provided request.
     * If a specific field in the request is null, the original value from the database is retained.
     * The updated diary is saved to the database and a response object is returned.
     *
     * @param diaryId The unique identifier of the diary to be edited.
     * @param req The request object containing updated diary attributes such as title, emoji, and color.
     * @return A response object containing the updated diary details including ID, title, creation timestamp, owner, emoji, and color.
     */
    public DiaryResponse editDiary(Long diaryId, DiaryEditRequest req){
        Diary diaryFromDB = getAccessibleDiary(diaryId);

        diaryFromDB.setTitle((req.getTitle() != null) ? req.getTitle() : diaryFromDB.getTitle());
        diaryFromDB.setEmoji((req.getEmoji() != null) ? req.getEmoji() : diaryFromDB.getEmoji());
        diaryFromDB.setColor((req.getColor() != null) ? req.getColor() : diaryFromDB.getColor());

        diaryRepository.save(diaryFromDB);
        DiaryResponse resp = new DiaryResponse();
        resp.setId(diaryFromDB.getDiaryId());
        resp.setTitle(diaryFromDB.getTitle());
        resp.setCreatedAt(diaryFromDB.getCreatedAt());
        resp.setOwner(diaryFromDB.getOwner().getUsername());
        resp.setEmoji(diaryFromDB.getEmoji());
        resp.setColor(diaryFromDB.getColor());

        return resp;
    }

    /**
     * Determines if a user has access to a given diary.
     *
     * @param diary The diary to be accessed.
     * @param userId The ID of the user attempting to access the diary.
     * @return true if the user is the owner or a collaborator of the diary, false otherwise.
     */
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

    //HELPERS

    /**
     * Extracts the user ID from the current authenticated user's JWT.
     *
     * @return the user ID extracted from the JWT as a Long.
     * @throws AccessDeniedException if the user is not authenticated.
     */
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

    /**
     * Retrieves a diary owned by the currently authenticated user.
     *
     * The method fetches the diary with the specified ID from the database.
     * If no diary is found with that ID, an exception is thrown.
     * Additionally, it validates that the current user is the owner of the diary;
     * if not, an AccessDeniedException is thrown.
     *
     * @param diaryId the ID of the diary to retrieve
     * @return the diary owned by the authenticated user
     * @throws RuntimeException if the diary does not exist
     * @throws AccessDeniedException if the authenticated user is not the owner of the diary
     */
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
