CREATE TABLE study_comment
(
    id                       BIGINT AUTO_INCREMENT NOT NULL,
    created_at               datetime              NULL,
    updated_at               datetime              NULL,
    study_id                 BIGINT                NOT NULL,
    member_id                BIGINT                NOT NULL,
    content                  VARCHAR(255)          NOT NULL,
    study_comment_parent_id  BIGINT                NULL,
    CONSTRAINT pk_study_comment PRIMARY KEY (id)
);

ALTER TABLE study_comment
    ADD CONSTRAINT FK_STUDY_COMMENT_ON_STUDY FOREIGN KEY (study_id) REFERENCES study (id);

ALTER TABLE study_comment
    ADD CONSTRAINT FK_STUDY_COMMENT_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE study_comment
    ADD CONSTRAINT FK_STUDY_COMMENT_ON_PARENT_STUDY_COMMENT FOREIGN KEY (study_comment_parent_id) REFERENCES study_comment (id);
