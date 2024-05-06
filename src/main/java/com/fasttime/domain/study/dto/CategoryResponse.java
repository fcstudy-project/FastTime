package com.fasttime.domain.study.dto;

import com.fasttime.domain.study.entity.Category;
import lombok.Builder;

@Builder
public record CategoryResponse(
        Long id,
        String name) {
    public static CategoryResponse of(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName()).build();
    }
}
