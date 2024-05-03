package com.fasttime.domain.resume.dto;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record ResumeResponseDto(
    Long id,
    String title,
    String content,
    String writer,
    int likeCount,
    int viewCount,
    LocalDateTime createdAt,
    LocalDateTime lastModifiedAt,
    LocalDateTime deletedAt
) {

    @Override
    public String toString() {
        return String.format(
            "id: %s, title: %s, content: %s, writer: %s, like count: %d, view count: %d, create at: %s, last modified: %s, delete at: %s",
            id, title, content, writer, likeCount, viewCount, createdAt, lastModifiedAt,
            deletedAt != null ? deletedAt : "N/A");
    }
}
