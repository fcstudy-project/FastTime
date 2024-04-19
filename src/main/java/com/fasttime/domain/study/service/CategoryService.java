package com.fasttime.domain.study.service;

import com.fasttime.domain.study.entity.Category;
import com.fasttime.domain.study.exception.CategoryNotFoundException;
import com.fasttime.domain.study.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(CategoryNotFoundException::new);
    }
}
