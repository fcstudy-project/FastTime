CREATE TABLE study_suggestion
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    status     enum ('CONSIDERING','APPROVE', 'REJECT') NOT NULL,
    member_id  BIGINT NULL,
    study_id   BIGINT NULL,
    message    VARCHAR(255) NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    deleted_at datetime NULL,
    CONSTRAINT pk_study_application PRIMARY KEY (id)
);

ALTER TABLE study_suggestion
    ADD CONSTRAINT FK_STUDY_SUGGESTION_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);

ALTER TABLE study_application
    ADD CONSTRAINT FK_STUDY_SUGGESTION_ON_STUDY FOREIGN KEY (study_id) REFERENCES study (id);
