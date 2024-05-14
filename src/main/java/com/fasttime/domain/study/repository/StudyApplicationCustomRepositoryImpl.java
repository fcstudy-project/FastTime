package com.fasttime.domain.study.repository;

import static com.fasttime.domain.member.entity.QMember.member;
import static com.fasttime.domain.study.entity.QStudy.study;
import static com.fasttime.domain.study.entity.QStudyApplication.studyApplication;

import com.fasttime.domain.study.dto.request.GetStudyApplicationsRequestDto;
import com.fasttime.domain.study.entity.StudyApplication;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

/**
 * 스터디 참여 신청 커스텀 레포지토리
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Repository
public class StudyApplicationCustomRepositoryImpl implements StudyApplicationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public StudyApplicationCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 스터디 참여 신청을 검색 조건에 따라 목록 조회하는 메서드
     *
     * @param getStudyApplicationsRequestDto 스터디 참여 신청 조회 요청 DTO
     * @param pageable                       페이지네이션을 위한 pageable 객체
     * @return 스터디 참여 신청 목록 페이지
     */
    @Override
    public Page<StudyApplication> findAllByConditions(
        GetStudyApplicationsRequestDto getStudyApplicationsRequestDto,
        Pageable pageable
    ) {
        List<StudyApplication> content = jpaQueryFactory
            .selectFrom(studyApplication)
            .leftJoin(studyApplication.study, study)
            .leftJoin(studyApplication.applicant, member)
            .fetchJoin()
            .where(createSearchConditionsBuilder(getStudyApplicationsRequestDto))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(studyApplication.createdAt.asc())
            .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(studyApplication.count())
            .from(studyApplication)
            .leftJoin(studyApplication.study, study)
            .leftJoin(studyApplication.applicant, member)
            .where(createSearchConditionsBuilder(getStudyApplicationsRequestDto));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 스터디 참여 신청 목록 조회 요청 DTO를 기반으로 검색 조건을 BooleanBuilder로 생성하는 메서드
     *
     * @param getStudyApplicationsRequestDto 스터디 참여 신청 목록 조회 요청 DTO
     * @return 스터디 참여 신청 검색 조건이 담긴 BooleanBuilder
     */
    private BooleanBuilder createSearchConditionsBuilder(
        GetStudyApplicationsRequestDto getStudyApplicationsRequestDto
    ) {
        BooleanBuilder bB = new BooleanBuilder();
        boolean isFindAllByStudyId = getStudyApplicationsRequestDto.studyId() != null;
        boolean isFindAllByApplicantId = getStudyApplicationsRequestDto.studyId() == null;
        if (isFindAllByStudyId) {
            bB.and(studyApplication.study.id.eq(getStudyApplicationsRequestDto.studyId()));
        }
        if (isFindAllByApplicantId) {
            bB.and(studyApplication.applicant.id.eq(getStudyApplicationsRequestDto.applicantId()));
        }
        return bB;
    }
}
