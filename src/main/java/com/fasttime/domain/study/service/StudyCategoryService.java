package com.fasttime.domain.study.service;

import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyCategory;
import com.fasttime.domain.study.repository.StudyCategoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StudyCategoryService {

    private final StudyCategoryRepository studyCategoryRepository;
    private final CategoryService categoryService;

    @Transactional
    public void createCategory(Study study, List<Long> categoryIds) {
        categoryIds.stream()
            .map(categoryService::getCategory)
            .forEach(category -> studyCategoryRepository.save(
                StudyCategory.builder()
                    .study(study)
                    .category(category)
                    .build()
            ));
    }

    @Transactional
    public void deleteCategoryByUpdate(Study study) {
        studyCategoryRepository.deleteAllInBatch(studyCategoryRepository.findAllByStudy(study));
    }

    public List<String> findCategoryNameByStudy(Study study) {
        List<StudyCategory> studyCategories = studyCategoryRepository.findAllByStudy(study);
        return studyCategories.stream()
            .map(studyCategory -> studyCategory.getCategory().getName()).toList();
    }

}
