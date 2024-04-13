package com.fasttime.domain.certification.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBootCamp is a Querydsl query type for BootCamp
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBootCamp extends EntityPathBase<BootCamp> {

    private static final long serialVersionUID = -1945666091L;

    public static final QBootCamp bootCamp = new QBootCamp("bootCamp");

    public final com.fasttime.global.common.QBaseTimeEntity _super = new com.fasttime.global.common.QBaseTimeEntity(this);

    public final StringPath course = createString("course");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> deletedAt = _super.deletedAt;

    public final StringPath description = createString("description");

    public final BooleanPath governmentFunded = createBoolean("governmentFunded");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath image = createString("image");

    public final StringPath name = createString("name");

    public final StringPath organizer = createString("organizer");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final StringPath website = createString("website");

    public QBootCamp(String variable) {
        super(BootCamp.class, forVariable(variable));
    }

    public QBootCamp(Path<? extends BootCamp> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBootCamp(PathMetadata metadata) {
        super(BootCamp.class, metadata);
    }

}

