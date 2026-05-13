package com.zimgo.colog.messages.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MessageRespond {

    private Long id;

    private String content;

    private LocalDateTime timestamp;

    private Long senderId;

    private String senderName;
}
