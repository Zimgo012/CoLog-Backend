package com.zimgo.colog.diary;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.messages.Messages;
import com.zimgo.colog.document.Document;
import com.zimgo.colog.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "diary")
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long diaryId;

    @Column
    private String title;

    @Column
    private boolean isPublic;

    @Column
    private LocalDate createdAt = LocalDate.now();

    @Column
    private LocalDate lastOpenedAt;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonIgnore
    private User owner;


    @ManyToMany(mappedBy = "collaboratedDiary")
    @JsonIgnore
    private List<User> collaborators = new ArrayList<>();

    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL)
    public List<Document> documents = new ArrayList<>();


}
