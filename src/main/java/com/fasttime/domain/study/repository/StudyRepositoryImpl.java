package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.entity.QStudy;
import com.fasttime.domain.study.entity.Study;
import com.fasttime.global.util.QueryDslUtil;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.LinkedList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;


public class StudyRepositoryImpl implements StudyRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;
    private final QStudy study = QStudy.study;

    public StudyRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Study> search(Pageable pageable) {
        List<Study> studies = jpaQueryFactory.selectFrom(study)
                .where(study.deletedAt.isNull())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(getAllOrderSpecifiers(pageable).toArray(OrderSpecifier[]::new)).fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
            .select(study.count())
            .from(study)
                .where(study.deletedAt.isNull());

        return PageableExecutionUtils.getPage(studies, pageable, countQuery::fetchOne);
    }

    private List<OrderSpecifier<?>> getAllOrderSpecifiers(Pageable pageable) {
        List<OrderSpecifier<?>> orders = new LinkedList<>();
        if (!pageable.getSort().isEmpty()) {
            for (Sort.Order order : pageable.getSort()) {
                Order direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
                switch (order.getProperty()) {
                    case "applicant":
                        orders.add(QueryDslUtil.getSortedColumn(direction, study, "applicant"));
                    case "createdAt":
                        orders.add(QueryDslUtil.getSortedColumn(direction, study, "createdAt"));
                    default:
                        orders.add(QueryDslUtil.getSortedColumn(Order.DESC, study, "createdAt"));
                }
            }
        }

        return orders;
    }
}
