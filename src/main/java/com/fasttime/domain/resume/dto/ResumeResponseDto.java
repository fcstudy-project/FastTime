package com.fasttime.domain.resume.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime lastModifiedAt,
    @JsonFormat(pattern = "yyyy-MM-dd") LocalDateTime deletedAt
) {

    @Override
    public String toString() {
        return String.format(
            "id: %s, title: %s, content: %s, writer: %s, like count: %d, view count: %d, created at: %s, last modified: %s, deleted at: %s",
            id, title, content, writer, likeCount, viewCount,
            createdAt != null ? createdAt.toString() : "N/A",
            lastModifiedAt != null ? lastModifiedAt.toString() : "N/A",
            deletedAt != null ? deletedAt.toString() : "N/A");
    }
}
