CREATE TABLE resume
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    deleted_at datetime NULL,
    member_id  BIGINT NULL,
    title      VARCHAR(255) NOT NULL,
    content    VARCHAR(255) NOT NULL,
    like_count INTEGER DEFAULT 0,
    view_count INTEGER DEFAULT 0,
    CONSTRAINT pk_resume PRIMARY KEY (id)
);

ALTER TABLE resume
    ADD CONSTRAINT FK_RESUME_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);
