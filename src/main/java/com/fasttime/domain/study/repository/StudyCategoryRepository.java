package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.entity.Study;
import com.fasttime.domain.study.entity.StudyCategory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyCategoryRepository extends JpaRepository<StudyCategory, Long> {

    List<StudyCategory> findAllByStudy(Study study);
}
