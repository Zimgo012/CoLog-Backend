package com.zimgo.colog.revision;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.document.Document;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Revision {

    @Id
    @GeneratedValue
    public Long id;

    @Column(name = "yjs_update", columnDefinition = "bytea")
    public byte[] yjsUpdate;

    @Column
    public LocalDateTime saveDate;

    @ManyToOne
    @JoinColumn(name="revisions")
    @JsonIgnore
    public Document document;


}
