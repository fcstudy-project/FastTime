package com.fasttime.global.batch.config;

import com.fasttime.domain.certification.repository.CertificationRepository;
import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.global.batch.tasklet.*;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;

    public BatchConfig(DataSource dataSource, PlatformTransactionManager transactionManager) {
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
    }

    @Bean
    public JobRepository jobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setDatabaseType("MYSQL");
        factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
        factory.setTablePrefix("BATCH_");
        factory.setMaxVarCharLength(2500);

        factory.afterPropertiesSet();
        return factory.getObject();
    }

    @Bean
    public DataFieldMaxValueIncrementer incrementer() {
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer(dataSource,
            "BATCH_JOB_SEQ", "ID");
        return incrementer;
    }

    @Bean
    public DataFieldMaxValueIncrementer jobExecutionIncrementer() {
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer(dataSource,
            "BATCH_JOB_EXECUTION_SEQ", "ID");
        return incrementer;
    }

    @Bean
    public DataFieldMaxValueIncrementer stepExecutionIncrementer() {
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer(dataSource,
            "BATCH_STEP_EXECUTION_SEQ", "ID");
        return incrementer;
    }

    @Bean
    public Job initJob() throws Exception {
        return new JobBuilder("initJob", jobRepository())
            .start(initStep())
            .build();
    }

    @Bean
    public Step initStep() throws Exception {
        return new StepBuilder("initStep", jobRepository())
            .tasklet((contribution, chunkContext) -> {
                // Sample data 삽입
                JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

                // Check if sample data already exists
                Long jobInstanceId = null;
                try {
                    jobInstanceId = jdbcTemplate.queryForObject(
                        "SELECT JOB_INSTANCE_ID FROM BATCH_JOB_INSTANCE WHERE JOB_NAME = ? AND JOB_KEY = ?",
                        Long.class, "sampleJob", "sampleKey");
                } catch (EmptyResultDataAccessException e) {
                    // Job instance does not exist
                }

                if (jobInstanceId == null) {
                    // Insert new job instance
                    jdbcTemplate.update(
                        "INSERT INTO BATCH_JOB_INSTANCE (JOB_NAME, JOB_KEY, VERSION) VALUES (?, ?, ?)",
                        "sampleJob", "sampleKey", 1);
                    // Retrieve the newly inserted job instance ID
                    jobInstanceId = jdbcTemplate.queryForObject(
                        "SELECT JOB_INSTANCE_ID FROM BATCH_JOB_INSTANCE WHERE JOB_NAME = ? AND JOB_KEY = ?",
                        Long.class, "sampleJob", "sampleKey");
                }

                // Insert job execution for the existing or newly created job instance
                jdbcTemplate.update(
                    "INSERT INTO BATCH_JOB_EXECUTION (JOB_INSTANCE_ID, VERSION, CREATE_TIME) VALUES (?, ?, NOW())",
                    jobInstanceId, 1);

                return RepeatStatus.FINISHED;
            }, transactionManager)
            .build();
    }

    @Bean
    public Job deleteOldReviewsJob(JobRepository jobRepository,
        @Qualifier("deleteOldReviewsStep") Step deleteOldReviewsStep) {
        return new JobBuilder("deleteOldReviewsJob", jobRepository)
            .start(deleteOldReviewsStep)
            .build();
    }

    @Bean
    public Step deleteOldReviewsStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, DeleteOldReviewsTasklet tasklet) {
        return new StepBuilder("deleteOldReviewsStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public DeleteOldReviewsTasklet deleteOldReviewsTasklet(ReviewRepository reviewRepository) {
        return new DeleteOldReviewsTasklet(reviewRepository);
    }

    @Bean
    public Job updateReferenceStatusJob(JobRepository jobRepository,
        @Qualifier("updateActivityStatusStep") Step updateActivityStatusStep,
        @Qualifier("updateCompetitionStatusStep") Step updateCompetitionStatusStep) {
        return new JobBuilder("updateReferenceStatusJob", jobRepository)
            .start(updateActivityStatusStep)
            .next(updateCompetitionStatusStep)
            .build();
    }

    @Bean
    public Job updateReferenceJob(JobRepository jobRepository,
        @Qualifier("updateNewActivityStep") Step updateNewActivityStep,
        @Qualifier("updateNewCompetitionStep") Step updateNewCompetitionStep,
        @Qualifier("updateDoneActivityStep") Step updateDoneActivityStep,
        @Qualifier("updateDoneCompetitionStep") Step updateDoneCompetitionStep) {

        return new JobBuilder("updateReferenceJob", jobRepository)
            .start(updateNewActivityStep)
            .next(updateNewCompetitionStep)
            .next(updateDoneActivityStep)
            .next(updateDoneCompetitionStep)
            .build();
    }

    @Bean
    public Step updateActivityStatusStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateActivityStatusTasklet tasklet) {
        return new StepBuilder("updateActivityStatusStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateCompetitionStatusStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateCompetitionStatusTasklet tasklet) {
        return new StepBuilder("updateCompetitionStatusStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateNewActivityStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateNewActivityTasklet tasklet) {
        return new StepBuilder("updateNewActivityStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateNewCompetitionStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateNewCompetitionTasklet tasklet) {
        return new StepBuilder("updateNewCompetitionStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateDoneActivityStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateDoneActivityTasklet tasklet) {
        return new StepBuilder("updateDoneActivityStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateDoneCompetitionStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, UpdateDoneCompetitionTasklet tasklet) {
        return new StepBuilder("updateDoneCompetitionStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public DeleteCertificationsTasklet deleteCertificationsTasklet(
        CertificationRepository certificationRepository) {
        return new DeleteCertificationsTasklet(certificationRepository);
    }

    @Bean
    public Job deleteCertificationsJob(JobRepository jobRepository,
        @Qualifier("deleteCertificationsStep") Step deleteCertificationsStep) {
        return new JobBuilder("deleteCertificationsJob", jobRepository)
            .start(deleteCertificationsStep)
            .build();
    }

    @Bean
    public Step deleteCertificationsStep(JobRepository jobRepository,
        PlatformTransactionManager transactionManager, DeleteCertificationsTasklet tasklet) {
        return new StepBuilder("deleteCertificationsStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Job updateResumeViewCountToDbJob(JobRepository jobRepository,
        @Qualifier("updateResumeViewCountStep") Step updateResumeViewCountStep) {
        return new JobBuilder("updateResumeViewCountToDbJob", jobRepository)
            .start(updateResumeViewCountStep)
            .build();
    }

    @Bean
    public Step updateResumeViewCountStep(JobRepository jobRepository,
        UpdateResumeViewCountTasklet tasklet, PlatformTransactionManager transactionManager) {
        return new StepBuilder("updateResumeViewCountStep", jobRepository)
            .tasklet(tasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }
}
