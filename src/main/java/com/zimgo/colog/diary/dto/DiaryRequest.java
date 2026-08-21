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
public class DiaryRequest {

    String title;
    boolean isPublic;
    LocalDate createdAt;
    LocalDate lastOpenedAt;
}
