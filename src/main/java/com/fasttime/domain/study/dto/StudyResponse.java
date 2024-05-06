package com.fasttime.domain.study.dto;

import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record StudyResponse(
    Long id,
    String title,
    String content,
    String skill,
    int total,
    int current,
    int applicant,
    LocalDate recruitmentStart,
    LocalDate recruitmentEnd,
    LocalDate progressStart,
    LocalDate progressEnd,
    String contact,
    String nickname,
    List<String> categories
) {

}
