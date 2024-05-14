package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.entity.StudyApplication;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 스터디 참여 신청 레포지토리
 * <p>
 * JPA 레포지토리와 스터디 참여 신청 커스텀 레포지토리를 상속받는 레포지토리 입니다.
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
public interface StudyApplicationRepository
    extends JpaRepository<StudyApplication, Long>, StudyApplicationCustomRepository {

}
