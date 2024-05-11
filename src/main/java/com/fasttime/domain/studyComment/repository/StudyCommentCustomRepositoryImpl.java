package com.fasttime.domain.studyComment.repository;

import com.fasttime.domain.studyComment.dto.request.GetStudyCommentsRequestDTO;
import com.fasttime.domain.studyComment.entity.QStudyComment;
import com.fasttime.domain.studyComment.entity.StudyComment;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.fasttime.domain.member.entity.QMember.member;
import static com.fasttime.domain.study.entity.QStudy.study;

@Repository
public class StudyCommentCustomRepositoryImpl implements StudyCommentCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QStudyComment studyComment = QStudyComment.studyComment;

    public StudyCommentCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<StudyComment> findAllBySearchCondition(GetStudyCommentsRequestDTO getStudyCommentsRequestDTO,
                                                       Pageable pageable) {
        List<StudyComment> content = jpaQueryFactory
            .selectFrom(studyComment)
            .leftJoin(studyComment.study, study)
            .leftJoin(studyComment.member, member)
            .leftJoin(studyComment.parentStudyComment)
            .fetchJoin()
            .where(createSearchConditionsBuilder(getStudyCommentsRequestDTO))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(studyComment.createdAt.asc())
            .fetch();
        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(studyComment.count())
            .from(studyComment)
            .leftJoin(studyComment.study, study)
            .leftJoin(studyComment.member, member)
            .leftJoin(studyComment.parentStudyComment)
            .where(createSearchConditionsBuilder(getStudyCommentsRequestDTO));
        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    @Override
    public Long countByStudyID(long studyId) {
        return jpaQueryFactory
            .select(studyComment.count())
            .from(studyComment)
            .leftJoin(studyComment.study, study)
            .where(studyComment.study.id.eq(studyId).and(studyComment.deletedAt.isNull()))
            .fetchOne();
    }

    private BooleanBuilder createSearchConditionsBuilder(
        GetStudyCommentsRequestDTO getStudyCommentsRequestDTO) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        boolean isFindAllByStudyId = getStudyCommentsRequestDTO.getStudyId() != null;
        boolean isFindAllByMemberId = getStudyCommentsRequestDTO.getMemberId() != null;
        boolean isFindAllByParentCommentId = getStudyCommentsRequestDTO.getParentStudyCommentId() != null;
        if (isFindAllByStudyId) {
            booleanBuilder.and(studyComment.study.id.eq(getStudyCommentsRequestDTO.getStudyId()));
            booleanBuilder.and(studyComment.parentStudyComment.isNull());
        }
        if (isFindAllByMemberId) {
            booleanBuilder.and(studyComment.member.id.eq(getStudyCommentsRequestDTO.getMemberId()));
        }
        if (isFindAllByParentCommentId) {
            booleanBuilder.and(
                    studyComment.parentStudyComment.id.eq(getStudyCommentsRequestDTO.getParentStudyCommentId()));
        }
        booleanBuilder.and(studyComment.deletedAt.isNull());
        return booleanBuilder;
    }
}
