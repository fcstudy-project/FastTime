package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.entity.Study;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long>,StudyRepositoryCustom {
}
