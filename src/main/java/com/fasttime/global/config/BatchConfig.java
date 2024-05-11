package com.fasttime.global.config;

import com.fasttime.domain.certification.repository.CertificationRepository;
import com.fasttime.domain.review.repository.ReviewRepository;
import com.fasttime.global.batch.tasklet.DeleteCertificationsTasklet;
import com.fasttime.global.batch.tasklet.DeleteOldReviewsTasklet;
import com.fasttime.global.batch.tasklet.UpdateActivityStatusTasklet;
import com.fasttime.global.batch.tasklet.UpdateCompetitionStatusTasklet;
import com.fasttime.global.batch.tasklet.UpdateDoneActivityTasklet;
import com.fasttime.global.batch.tasklet.UpdateDoneCompetitionTasklet;
import com.fasttime.global.batch.tasklet.UpdateNewActivityTasklet;
import com.fasttime.global.batch.tasklet.UpdateNewCompetitionTasklet;
import com.fasttime.global.batch.tasklet.UpdateResumeViewCountTasklet;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

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
        @Qualifier("updateNewActivityStep") Step deleteCertificationsStep) {
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
            .allowStartIfComplete(Boolean.TRUE)
            .build();
    }
}
