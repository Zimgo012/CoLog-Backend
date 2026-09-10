package com.zimgo.colog.diary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DiaryCollaboratorResponse {

    Long diaryId;
    String title;
    String diayOwnerName;
    Long userId;
    String name;
    String email;
    String username;

}
