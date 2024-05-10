package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.entity.StudySuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudySuggestionRepository
    extends JpaRepository<StudySuggestion, Long>, StudySuggestionCustomRepository {

}
