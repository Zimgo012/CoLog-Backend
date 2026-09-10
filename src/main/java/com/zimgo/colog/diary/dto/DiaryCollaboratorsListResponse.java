package com.zimgo.colog.diary.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DiaryCollaboratorsListResponse {

    Long id;
    String username;
    String name;
    String email;
}
