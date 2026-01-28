package com.zimgo.colog.diary;

import com.zimgo.colog.collaborator.Collaborator;
import com.zimgo.colog.comment.Comment;
import com.zimgo.colog.user.User;
import jakarta.persistence.*;
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

    @Column
    private boolean isPublic;

    @Column
    private Date createdAt;

    @Column
    private Date modifiedAt;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany
    private List<Comment> comments;

//    @OneToMany
//    private List<Collaborator> collaborators;


}
