package com.fasttime.domain.study.repository;

import static com.fasttime.domain.member.entity.QMember.member;
import static com.fasttime.domain.study.entity.QStudy.study;
import static com.fasttime.domain.study.entity.QStudyApplication.studyApplication;
import static com.fasttime.domain.study.entity.QStudySuggestion.studySuggestion;

import com.fasttime.domain.study.dto.request.GetStudySuggestionsRequestDto;
import com.fasttime.domain.study.entity.StudySuggestion;
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
public class StudySuggestionCustomRepositoryImpl implements StudySuggestionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public StudySuggestionCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<StudySuggestion> findAllByConditions(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto,
        Pageable pageable
    ) {
        List<StudySuggestion> content = jpaQueryFactory
            .selectFrom(studySuggestion)
            .leftJoin(studySuggestion.study, study)
            .leftJoin(studySuggestion.receiver, member)
            .fetchJoin()
            .where(createSearchConditionsBuilder(getStudySuggestionsRequestDto))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(studySuggestion.createdAt.asc())
            .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(studySuggestion.count())
            .from(studySuggestion)
            .leftJoin(studySuggestion.study, study)
            .leftJoin(studySuggestion.receiver, member)
            .where(createSearchConditionsBuilder(getStudySuggestionsRequestDto));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanBuilder createSearchConditionsBuilder(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto
    ) {
        BooleanBuilder bB = new BooleanBuilder();
        boolean isFindAllByStudyId = getStudySuggestionsRequestDto.studyId() != null;
        boolean isFindAllByReceiverId = getStudySuggestionsRequestDto.studyId() == null;
        if (isFindAllByStudyId) {
            bB.and(studyApplication.study.id.eq(getStudySuggestionsRequestDto.studyId()));
        }
        if (isFindAllByReceiverId) {
            bB.and(studyApplication.applicant.id.eq(getStudySuggestionsRequestDto.receiverId()));
        }
        return bB;
    }
}