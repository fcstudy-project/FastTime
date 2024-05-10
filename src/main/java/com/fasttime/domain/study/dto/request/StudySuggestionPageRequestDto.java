package com.fasttime.domain.study.dto.request;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;

public record StudySuggestionPageRequestDto(
    int page,
    int size
) {

    @Builder
    public StudySuggestionPageRequestDto(int page, int size) {
        this.page = Math.max(page, 0);
        int DEFAULT_SIZE = 10;
        int MAX_SIZE = 50;
        this.size = size > MAX_SIZE ? DEFAULT_SIZE : size;
    }

    public PageRequest of() {
        return PageRequest.of(page, size);
    }
}
