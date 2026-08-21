package com.zimgo.colog.diary.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DiaryResponse {
    String title;
    boolean isPublic;
    LocalDate dateCreated;

}
