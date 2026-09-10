package com.zimgo.colog.diary.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DiaryEditRequest {
    @Pattern(regexp = ".*\\S.*", message = "Title must not be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    String title;

    @Size(max = 32, message = "Emoji must not exceed 32 characters")
    String emoji;

    @Size(max = 50, message = "Color must not exceed 50 characters")
    String color;
}
