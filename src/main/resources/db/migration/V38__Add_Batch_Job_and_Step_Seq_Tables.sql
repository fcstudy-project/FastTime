-- Sequence table for managing job execution IDs in Spring Batch
CREATE TABLE BATCH_JOB_EXECUTION_SEQ
(
    ID_NAME VARCHAR(50) NOT NULL,
    ID      BIGINT      NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (ID),
    UNIQUE (ID_NAME)
);

-- Sequence table for managing step execution IDs in Spring Batch
CREATE TABLE BATCH_STEP_EXECUTION_SEQ
(
    ID_NAME VARCHAR(50) NOT NULL,
    ID      BIGINT      NOT NULL AUTO_INCREMENT,
    PRIMARY KEY (ID),
    UNIQUE (ID_NAME)
);
