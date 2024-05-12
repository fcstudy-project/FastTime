package com.fasttime.domain.study.controller;


import com.fasttime.domain.study.dto.CategoryResponse;
import com.fasttime.domain.study.service.CategoryService;
import com.fasttime.global.util.ResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v2/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<ResponseDTO<List<CategoryResponse>>> getAllCategories() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(ResponseDTO.res(HttpStatus.OK,categoryService.getAllCategories()));
    }
}
