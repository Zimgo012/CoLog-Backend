package com.zimgo.colog.diary;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
    private Long id;

    @Column
    private String title;

    @Column
    private boolean isPublic;

    @Column
    private LocalDate createdAt = LocalDate.now();

    @Column
    private LocalDate modifiedAt;

    @ManyToOne
    @JoinColumn(name = "ownerId")
    @JsonIgnore
    private User owner;

    @ManyToMany(mappedBy = "collaboratedDiary")
    @JsonIgnore
    private List<User> collaborators;

//
//    @OneToMany
//    private List<Comment> comments;

//    @OneToMany
//    private List<Collaborator> collaborators;


}
