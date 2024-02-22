package com.fasttime.domain.reference.repository;

import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {

    List<Competition> findAllByStatus(RecruitmentStatus status);

    Optional<Competition> findByTitle(String title);
}