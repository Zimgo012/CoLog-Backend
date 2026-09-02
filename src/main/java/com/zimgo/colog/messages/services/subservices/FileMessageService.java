package com.zimgo.colog.messages.services.subservices;

import com.zimgo.colog.diary.DiaryService;
import com.zimgo.colog.chat.ChatRepository;
import com.zimgo.colog.messages.dto.MessageRequest;
import com.zimgo.colog.user.UserService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class FileMessageService {


    public FileMessageService() {}

    public void processFileMessage(Long diaryId, MessageRequest request){

    }
}
