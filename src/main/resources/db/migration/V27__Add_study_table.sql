CREATE TABLE study
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    title               VARCHAR(255) NOT NULL,
    content             VARCHAR(255) NOT NULL,
    skill               VARCHAR(255) NOT NULL,
    total               INT          NOT NULL,
    member_id           BIGINT       NULL,
    current             INT          NOT NULL,
    applicant           INT          NOT NULL,
    recruitment_start   DATE          NOT NULL,
    recruitment_end     DATE          NOT NULL,
    progress_start      DATE          NOT NULL,
    progress_end        DATE          NOT NULL,
    contact             VARCHAR(255) NOT NULL,
    studyState          enum ('DURING','CLOSED') NOT NULL,
    CONSTRAINT pk_study PRIMARY KEY (id)
);

ALTER TABLE study
    ADD CONSTRAINT FK_STUDY_ON_MEMBER FOREIGN KEY (member_id) REFERENCES member (id);