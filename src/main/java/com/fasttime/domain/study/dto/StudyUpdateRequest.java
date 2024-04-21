package com.fasttime.domain.study.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record StudyUpdateRequest(
    @NotBlank
    String title,
    @NotBlank
    String content,
    @NotBlank
    String skill,
    @NotNull
    int total,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    LocalDate recruitmentEnd,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    LocalDate progressStart,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    LocalDate progressEnd,
    @NotBlank
    String contact,
    List<Long> categoryIds) {

}
