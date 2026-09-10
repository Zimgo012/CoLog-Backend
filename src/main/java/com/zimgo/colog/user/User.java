package com.zimgo.colog.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.comment.Comment;
import com.zimgo.colog.diary.Diary;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@ToString
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long userId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    @Enumerated(EnumType.STRING)
    private UserRole role;


    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    @Nullable
    @JsonIgnore
    private List<Diary> ownedDiary;

    @JsonIgnore
    @ManyToMany(mappedBy = "collaborators")
    private List<Diary> collaboratedDiary;

}
