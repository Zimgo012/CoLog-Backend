package com.zimgo.colog.diary;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

//    @ManyToOne
//    @JoinColumn(name = "owner_id")
//    private User owner;
//
//    @OneToMany
//    private List<Comment> comments;

//    @OneToMany
//    private List<Collaborator> collaborators;


}
