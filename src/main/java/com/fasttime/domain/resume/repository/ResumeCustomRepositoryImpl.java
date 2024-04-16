package com.fasttime.domain.resume.repository;

import static com.fasttime.domain.resume.entity.QResume.resume;

import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.entity.Resume;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class ResumeCustomRepositoryImpl implements ResumeCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public ResumeCustomRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<Resume> search(ResumesSearchRequest searchCondition) {
        return jpaQueryFactory.selectFrom(resume)
                .where(createResumeSearchCondition(searchCondition))
                .offset((long) searchCondition.page() * searchCondition.pageSize())
                .limit(searchCondition.pageSize())
                .orderBy(orderSpecifier(searchCondition))
                .fetch();
    }

    private BooleanBuilder createResumeSearchCondition(ResumesSearchRequest searchCondition) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(resume.deletedAt.isNull());
        return booleanBuilder;
    }

    private OrderSpecifier<?> orderSpecifier(ResumesSearchRequest searchCondition) {
        Order direction = Order.DESC;
        return switch (searchCondition.orderBy()) {
            // 좋아요순
            case ResumeOrderBy.LIKE_COUNT -> new OrderSpecifier<>(direction, resume.likeCount);
            // 조회순
            case ResumeOrderBy.VIEW_COUNT -> new OrderSpecifier<>(direction, resume.viewCount);
            // 최신순
            case null, default -> new OrderSpecifier<>(direction, resume.createdAt);
        };
    }
}
