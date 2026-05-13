package com.zimgo.colog.messages;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Messages {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private MessageType type;

    private String content;

    private LocalDateTime timestamp;

    @ManyToOne
    private User sender;

    @ManyToOne
    @JsonIgnore
    private Diary diaryId;


}
