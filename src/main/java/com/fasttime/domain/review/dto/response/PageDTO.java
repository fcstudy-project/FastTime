package com.fasttime.domain.review.dto.response;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@NoArgsConstructor
@Getter
public class PageDTO<T> {

    private int currentPage;
    private int totalPages;
    private int currentElements;
    private long totalElements;
    private List<T> reviews;

    public PageDTO(int currentPage, int totalPages, int currentElements, long totalElements,
        List<T> reviews) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.currentElements = currentElements;
        this.totalElements = totalElements;
        this.reviews = reviews;
    }

    public static <T> PageDTO<T> fromPage(Page<T> page) {
        return new PageDTO<>(
            page.getNumber() + 1,
            page.getTotalPages(),
            page.getNumberOfElements(),
            page.getTotalElements(),
            page.getContent()
        );
    }
}
