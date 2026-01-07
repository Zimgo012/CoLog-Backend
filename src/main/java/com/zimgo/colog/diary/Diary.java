package com.zimgo.colog.diary;

import com.zimgo.colog.comment.Comment;
import com.zimgo.colog.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Diary {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

//    private User owner;
//    private List<Comment> comments;
    private boolean isPublic;
    private Date createdAt;
    private Date modifiedAt;


}
