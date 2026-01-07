package com.zimgo.colog.comment;

import com.zimgo.colog.diary.Diary;
import com.zimgo.colog.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

    public String content;
//    public User author;
//    public Diary diaryIn;
    public Date createdAt;
    public Date modifiedAt;
}
