package com.zimgo.colog.messages.dto.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatPayload {
    private Long documentId;

    private String content;

    private Long senderId;
}
