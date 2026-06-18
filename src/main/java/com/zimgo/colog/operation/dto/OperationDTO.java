package com.zimgo.colog.operation.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class OperationDTO {
    private Long documentId;
    private Long deltaLogId;
    private String operationType;
    private int index;
    private String text;
    private int length;
    private LocalDateTime timestamp;
}