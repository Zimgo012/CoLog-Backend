package com.zimgo.colog.messages.dto;


import com.zimgo.colog.messages.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

    private String content;

    private Long senderId;

    private MessageType type;
}
