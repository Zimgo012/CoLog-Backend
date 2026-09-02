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
    Long id;
    String title;
    LocalDate createdAt;
    String owner;
    String emoji;
    String color;

}
