package com.zimgo.colog.user;

import com.zimgo.colog.comment.Comment;
import com.zimgo.colog.diary.Diary;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    //implement this later
//    private String hashPassword;
//    private List<Diary> diary;
//private Collaborator collaborator;
//    private List<Comment> comments;


}
