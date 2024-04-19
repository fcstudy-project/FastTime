package com.fasttime.domain.study.dto;

import lombok.Builder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Builder
public record StudyPageRequestService(
    String orderBy,
    int page,
    int pageSize
) {

    @Builder
    public StudyPageRequestService(
        String orderBy,
        int page,
        int pageSize
    ) {
        this.orderBy = orderBy;
        this.page = Math.max(page, 0);
        this.pageSize = pageSize > 20 ? 10 : pageSize;
    }

    public PageRequest toPageable() {
        return switch (orderBy) {
            case "applicant" -> PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "applicant"));
            case "latest" -> PageRequest.of(page, pageSize, Sort.by(Direction.DESC, "createdAt"));
            default -> PageRequest.of(page, pageSize);
        };
    }
}
