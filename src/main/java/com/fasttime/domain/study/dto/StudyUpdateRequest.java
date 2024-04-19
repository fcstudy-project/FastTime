package com.fasttime.domain.study.dto;

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
    @NotBlank
    LocalDate recruitmentEnd,
    @NotBlank
    LocalDate progressStart,
    @NotBlank
    LocalDate progressEnd,
    @NotBlank
    String contact,
    List<Long> categoryIds) {

}
