package com.fasttime.domain.certification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCertification is a Querydsl query type for Certification
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCertification extends EntityPathBase<Certification> {

    private static final long serialVersionUID = 522924216L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCertification certification = new QCertification("certification");

    public final com.fasttime.global.common.QBaseTimeEntity _super = new com.fasttime.global.common.QBaseTimeEntity(this);

    public final QBootCamp bootCamp;

    public final StringPath bootcampName = createString("bootcampName");

    public final StringPath content = createString("content");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final com.fasttime.domain.member.entity.QMember member;

    public final StringPath rejectionReason = createString("rejectionReason");

    public final EnumPath<CertificationStatus> status = createEnum("status", CertificationStatus.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath withdrawalReason = createString("withdrawalReason");

    public QCertification(String variable) {
        this(Certification.class, forVariable(variable), INITS);
    }

    public QCertification(Path<? extends Certification> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCertification(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCertification(PathMetadata metadata, PathInits inits) {
        this(Certification.class, metadata, inits);
    }

    public QCertification(Class<? extends Certification> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.bootCamp = inits.isInitialized("bootCamp") ? new QBootCamp(forProperty("bootCamp")) : null;
        this.member = inits.isInitialized("member") ? new com.fasttime.domain.member.entity.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

