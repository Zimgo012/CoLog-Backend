package com.zimgo.colog.messages.dto.payloads;

import com.zimgo.colog.messages.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PresencePayload {

    private Long senderId;

    private Long documentId;

    private String color;

    private int cursor;

    private int selectionStart;

    private int selectionEnd;

    private boolean typing;

    private long lastActive;

}
