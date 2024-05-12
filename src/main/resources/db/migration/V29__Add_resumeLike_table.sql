CREATE TABLE resume_like
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    deleted_at datetime NULL,
    member_id  BIGINT NULL,
    resume_id  BIGINT NULL,
    CONSTRAINT pk_like PRIMARY KEY (id)
);

ALTER TABLE resume_like
    ADD CONSTRAINT FK_LIKE_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);
ALTER TABLE resume_like
    ADD CONSTRAINT FK_LIKE_ON_RESUME FOREIGN KEY (resume_id) REFERENCES resume (id);
