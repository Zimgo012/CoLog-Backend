package com.zimgo.colog.messages.dto;


import com.zimgo.colog.messages.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {

    @NotNull(message = "Message type is required")
    private MessageType type;

    private Object payload;
}
