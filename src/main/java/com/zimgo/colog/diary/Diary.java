package com.zimgo.colog.diary;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.zimgo.colog.messages.Messages;
import com.zimgo.colog.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;

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
    private String content;

    //Temporary
    @OneToMany(mappedBy = "diaryId", cascade = CascadeType.ALL)
    private List<Messages> messages;

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


}
