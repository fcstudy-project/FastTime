CREATE TABLE view_count
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    resume_id  BIGINT                NULL,
    address    varchar(255)          NOT NULL,
    created_at datetime              NULL,
    updated_at datetime              NULL,
    deleted_at datetime              NULL,
    CONSTRAINT pk_like PRIMARY KEY (id)
);

ALTER TABLE view_count
    ADD Constraint FK_VIEW_COUNT_ON_RESUME FOREIGN KEY (resume_id) REFERENCES resume (id);
