package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.entity.StudySuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 스터디 참여 제안 레포지토리
 * <p>
 * JPA 레포지토리와 스터디 참여 제안 커스텀 레포지토리를 상속받는 레포지토리 입니다.
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface StudySuggestionRepository
    extends JpaRepository<StudySuggestion, Long>, StudySuggestionCustomRepository {

}
