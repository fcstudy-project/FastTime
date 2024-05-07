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

@Repository
public class StudyApplicationCustomRepositoryImpl implements StudyApplicationCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public StudyApplicationCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

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
