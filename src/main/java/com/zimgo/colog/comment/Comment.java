package com.zimgo.colog.comment;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long id;

    @Column
    public String content;

    @Column
    public Date createdAt;

    @Column
    public Date modifiedAt;

    @ManyToOne
    @JoinColumn(name = "author_id")
    public User author;

    @ManyToOne
    @JoinColumn(name = "diary_id")
    public Diary diary;
}
