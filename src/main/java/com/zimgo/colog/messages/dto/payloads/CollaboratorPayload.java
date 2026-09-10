package com.zimgo.colog.messages.dto.payloads;

import com.zimgo.colog.messages.dto.payloads.enums.CollaboratorOperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CollaboratorPayload {
    Long diaryId;
    String diaryTitle;
    String diaryOwnerName;
    String name;
    String email;
    String username;
    CollaboratorOperationType type;

}
