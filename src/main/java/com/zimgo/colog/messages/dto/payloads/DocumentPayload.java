package com.zimgo.colog.messages.dto.payloads;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class DocumentPayload {

    private String content;

    private Long senderId;

    private Long documentId;
}
