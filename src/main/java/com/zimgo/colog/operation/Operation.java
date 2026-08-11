package com.zimgo.colog.operation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.deltaLog.DeltaLog;
import com.zimgo.colog.document.Document;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long operationId;

    //Document
    @ManyToOne
    private Document document;

    //Base revision
    @ManyToOne
    @JsonIgnore
    private DeltaLog deltaLog;

    // TODO: string for now  change this later
    // private String user;

    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    private Long baseRevision;
    private Long appliedRevision;

    //payload
    private int index;
    private String text;
    private int length;
    private LocalDateTime timestamp;

}
