package com.zimgo.colog.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatResponse {
    Long id;
    String content;
    LocalDateTime timestamp;

    Long userSenderId;
    String userSenderName;

}
