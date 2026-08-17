/**
 * Acts like a page inside the diary.
 */

package com.zimgo.colog.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.diary.Diary;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long documentId;

    @Column
    public LocalDateTime date;

    @ManyToOne
    @JsonIgnore
    public Diary diary;

    @Column(
            name = "yjs_state",
            columnDefinition = "bytea"
    )
    private byte[] yjsState;


}
