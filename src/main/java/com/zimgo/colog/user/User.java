package com.zimgo.colog.user;

import com.zimgo.colog.comment.Comment;
import com.zimgo.colog.diary.Diary;
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
    private Long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String email;
    //implement this later
//    private String hashPassword;

//    @OneToMany
//    private List<Diary> diary;
//
//    //private Collaborator collaborator;
//    @OneToMany
//    private List<Comment> comments;


}
