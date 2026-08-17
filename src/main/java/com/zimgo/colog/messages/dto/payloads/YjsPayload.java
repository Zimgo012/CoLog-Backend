package com.zimgo.colog.messages.dto.payloads;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class YjsPayload {

    private Long senderId;

    private Long documentId;

    private byte[] yjs;
}
