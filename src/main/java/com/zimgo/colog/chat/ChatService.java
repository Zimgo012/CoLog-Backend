package com.zimgo.colog.chat;

import com.zimgo.colog.chat.dto.ChatResponse;
import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.exception.AppException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    private final ChatRepository chatRepository;
    private final DiaryService diaryService;

    public ChatService(ChatRepository chatRepository,
                       DiaryService diaryService){
        this.chatRepository = chatRepository;
        this.diaryService = diaryService;
    }

    public List<ChatResponse> getChatHistory(Long diaryId) {

        Diary diary = diaryService.getAccessibleDiary(diaryId);

        if (diary == null) {
            throw new AppException(HttpStatus.FORBIDDEN, "DIARY_ACCESS_DENIED", "You do not have permission to access this diary");
        }

        List<Chat> messages =
                chatRepository.findByDiaryOrderByTimestampAsc(diary);

        return messages.stream().map(info -> new ChatResponse(
                info.getMessageId(),
                info.getContent(),
                info.getTimestamp(),
                info.getSender().getUserId(),
                info.getSender().getFirstName() + " " + info.getSender().getLastName()
        )).toList();
    }

}
