CREATE TABLE study_category
(
    id               BIGINT AUTO_INCREMENT NOT NULL,
    CONSTRAINT pk_study_category PRIMARY KEY (id)
);

ALTER TABLE study_category
    ADD CONSTRAINT FK_STUDY_CATEGORY_ON_STUDY FOREIGN KEY (study_id) REFERENCES study (id);

ALTER TABLE study_category
    ADD CONSTRAINT FK_STUDY_CATEGORY_ON_CATEGORY FOREIGN KEY (category_id) REFERENCES category (id);