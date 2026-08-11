package com.zimgo.colog.messages.dto.payloads;

import com.zimgo.colog.messages.dto.payloads.enums.OperationCommandType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Setter
@Getter
public class OperationPayload {
    private Long documentId;
    private Long senderId;

    // Content of the operation
    private String content;

    //Delete, Insert, Format,
    //Update - save delta log
    private OperationCommandType operation;

    private int index; //where the operation is applied
    private String text; // the text that is inserted
    private int length; // the length of the text that is inserted

    //base revision
    private Long baseRevision;

}

