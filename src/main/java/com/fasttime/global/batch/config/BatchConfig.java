package com.fasttime.global.batch.config;

import com.fasttime.domain.certification.repository.CertificationRepository;
import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.global.batch.tasklet.*;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.support.incrementer.DataFieldMaxValueIncrementer;
import org.springframework.jdbc.support.incrementer.MySQLMaxValueIncrementer;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    public JobRepository jobRepository(DataSource dataSource,
        PlatformTransactionManager transactionManager) throws Exception {
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
    public DataFieldMaxValueIncrementer incrementer(DataSource dataSource) {
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer(dataSource, "BATCH_JOB_SEQ", "ID");
        return incrementer;
    }

    @Bean
    public DataFieldMaxValueIncrementer jobExecutionIncrementer(DataSource dataSource) {
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer(dataSource, "BATCH_JOB_EXECUTION_SEQ", "ID");
        return incrementer;
    }

    @Bean
    public DataFieldMaxValueIncrementer stepExecutionIncrementer(DataSource dataSource) {
        MySQLMaxValueIncrementer incrementer = new MySQLMaxValueIncrementer(dataSource, "BATCH_STEP_EXECUTION_SEQ", "ID");
        return incrementer;
    }

    @Bean
    public Job deleteOldReviewsJob(JobBuilderFactory jobBuilderFactory,
        @Qualifier("deleteOldReviewsStep") Step deleteOldReviewsStep) {
        return jobBuilderFactory.get("deleteOldReviewsJob")
            .start(deleteOldReviewsStep)
            .build();
    }

    @Bean
    public Step deleteOldReviewsStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, DeleteOldReviewsTasklet tasklet) {
        return stepBuilderFactory.get("deleteOldReviewsStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public DeleteOldReviewsTasklet deleteOldReviewsTasklet(ReviewRepository reviewRepository) {
        return new DeleteOldReviewsTasklet(reviewRepository);
    }

    @Bean
    public Job updateReferenceStatusJob(JobBuilderFactory jobBuilderFactory,
        @Qualifier("updateActivityStatusStep") Step updateActivityStatusStep,
        @Qualifier("updateCompetitionStatusStep") Step updateCompetitionStatusStep) {
        return jobBuilderFactory.get("updateReferenceStatusJob")
            .start(updateActivityStatusStep)
            .next(updateCompetitionStatusStep)
            .build();
    }

    @Bean
    public Job updateReferenceJob(JobBuilderFactory jobBuilderFactory,
        @Qualifier("updateNewActivityStep") Step updateNewActivityStep,
        @Qualifier("updateNewCompetitionStep") Step updateNewCompetitionStep,
        @Qualifier("updateDoneActivityStep") Step updateDoneActivityStep,
        @Qualifier("updateDoneCompetitionStep") Step updateDoneCompetitionStep) {

        return jobBuilderFactory.get("updateReferenceJob")
            .start(updateNewActivityStep)
            .next(updateNewCompetitionStep)
            .next(updateDoneActivityStep)
            .next(updateDoneCompetitionStep)
            .build();
    }

    @Bean
    public Step updateActivityStatusStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, UpdateActivityStatusTasklet tasklet) {
        return stepBuilderFactory.get("updateActivityStatusStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateCompetitionStatusStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, UpdateCompetitionStatusTasklet tasklet) {
        return stepBuilderFactory.get("updateCompetitionStatusStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateNewActivityStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, UpdateNewActivityTasklet tasklet) {
        return stepBuilderFactory.get("updateNewActivityStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateNewCompetitionStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, UpdateNewCompetitionTasklet tasklet) {
        return stepBuilderFactory.get("updateNewCompetitionStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateDoneActivityStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, UpdateDoneActivityTasklet tasklet) {
        return stepBuilderFactory.get("updateDoneActivityStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Step updateDoneCompetitionStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, UpdateDoneCompetitionTasklet tasklet) {
        return stepBuilderFactory.get("updateDoneCompetitionStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public DeleteCertificationsTasklet deleteCertificationsTasklet(
        CertificationRepository certificationRepository) {
        return new DeleteCertificationsTasklet(certificationRepository);
    }

    @Bean
    public Job deleteCertificationsJob(JobBuilderFactory jobBuilderFactory,
        @Qualifier("deleteCertificationsStep") Step deleteCertificationsStep) {
        return jobBuilderFactory.get("deleteCertificationsJob")
            .start(deleteCertificationsStep)
            .build();
    }

    @Bean
    public Step deleteCertificationsStep(StepBuilderFactory stepBuilderFactory,
        PlatformTransactionManager transactionManager, DeleteCertificationsTasklet tasklet) {
        return stepBuilderFactory.get("deleteCertificationsStep")
            .tasklet(tasklet, transactionManager)
            .build();
    }

    @Bean
    public Job updateResumeViewCountToDbJob(JobBuilderFactory jobBuilderFactory,
        @Qualifier("updateResumeViewCountStep") Step updateResumeViewCountStep) {
        return jobBuilderFactory.get("updateResumeViewCountToDbJob")
            .start(updateResumeViewCountStep)
            .build();
    }

    @Bean
    public Step updateResumeViewCountStep(StepBuilderFactory stepBuilderFactory,
        UpdateResumeViewCountTasklet tasklet, PlatformTransactionManager transactionManager) {
        return stepBuilderFactory.get("updateResumeViewCountStep")
            .tasklet(tasklet, transactionManager)
            .allowStartIfComplete(true)
            .build();
    }
}
