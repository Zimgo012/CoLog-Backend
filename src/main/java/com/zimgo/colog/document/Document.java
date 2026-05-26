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

    @Column(length = 1000)
    public String content;

    @ManyToOne
    @JsonIgnore
    public Diary diary;

}
