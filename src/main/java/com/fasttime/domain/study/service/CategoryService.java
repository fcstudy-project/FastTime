package com.fasttime.domain.study.service;

import com.fasttime.domain.study.dto.CategoryResponse;
import com.fasttime.domain.study.entity.Category;
import com.fasttime.domain.study.exception.CategoryNotFoundException;
import com.fasttime.domain.study.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(CategoryNotFoundException::new);
    }
    public List<CategoryResponse> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream().map(CategoryResponse::of).toList();
    }
}
