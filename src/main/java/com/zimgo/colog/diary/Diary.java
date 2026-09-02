package com.zimgo.colog.diary;


import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private LocalDate createdAt = LocalDate.now();

    @Column
    private LocalDate lastOpenedAt;

    @Column
    private String color;

    @Column
    private String emoji;

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
