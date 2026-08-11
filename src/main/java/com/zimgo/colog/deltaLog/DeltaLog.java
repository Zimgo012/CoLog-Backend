package com.zimgo.colog.deltaLog;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.operation.Operation;
import com.zimgo.colog.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class DeltaLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long deltaLogId;

    @ManyToOne
    @JsonIgnore
    private Document document;

    // TODO: string for now  change this late to
//    private String sender;

    @OneToMany(mappedBy = "deltaLog", cascade = CascadeType.ALL)
    private List<Operation> operations;

    // pointer to the actual file
    private String contentPointer;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    private Long startRevision = 0L;
    private Long endRevision = 0L;

}
