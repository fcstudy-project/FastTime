CREATE TABLE notification
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    type       enum ('STUDY_APPLICATION','STUDY_SUGGEST','STUDY_APPROVE', 'STUDY_REJECT') NOT NULL,
    content    VARCHAR(255) NULL,
    url        VARCHAR(255) NULL,
    is_read    BIT(1) NOT NULL,
    member_id  BIGINT NULL,
    created_at datetime NULL,
    updated_at datetime NULL,
    deleted_at datetime NULL,
    CONSTRAINT pk_notification PRIMARY KEY (id)
);

ALTER TABLE notification
    ADD CONSTRAINT FK_NOTIFICATION_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);
