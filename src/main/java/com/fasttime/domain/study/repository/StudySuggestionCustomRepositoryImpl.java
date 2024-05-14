package com.fasttime.domain.study.repository;

import static com.fasttime.domain.member.entity.QMember.member;
import static com.fasttime.domain.study.entity.QStudy.study;
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

/**
 * 스터디 참여 제안 커스텀 레포지토리
 *
 * @author JeongUijeong (jeong275117@gmail.com)
 */
@Repository
public class StudySuggestionCustomRepositoryImpl implements StudySuggestionCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public StudySuggestionCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    /**
     * 스터디 참여 제안을 검색 조건에 따라 목록 조회하는 메서드
     *
     * @param getStudySuggestionsRequestDto 스터디 참여 제안 조회 요청 DTO
     * @param pageable                      페이지네이션을 위한 pageable 객체
     * @return 스터디 참여 제안 목록 페이지
     */
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

    /**
     * 스터디 참여 제안 목록 조회 요청 DTO를 기반으로 검색 조건을 BooleanBuilder로 생성하는 메서드
     *
     * @param getStudySuggestionsRequestDto 스터디 참여 제안 목록 조회 요청 DTO
     * @return 스터디 참여 제안 검색 조건이 담긴 BooleanBuilder
     */
    private BooleanBuilder createSearchConditionsBuilder(
        GetStudySuggestionsRequestDto getStudySuggestionsRequestDto
    ) {
        BooleanBuilder bB = new BooleanBuilder();
        boolean isFindAllByStudyId = getStudySuggestionsRequestDto.studyId() != null;
        boolean isFindAllByReceiverId = getStudySuggestionsRequestDto.studyId() == null;
        if (isFindAllByStudyId) {
            bB.and(studySuggestion.study.id.eq(getStudySuggestionsRequestDto.studyId()));
        }
        if (isFindAllByReceiverId) {
            bB.and(studySuggestion.receiver.id.eq(getStudySuggestionsRequestDto.receiverId()));
        }
        return bB;
    }
}
