package com.fasttime.global.batch.scheduler;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job deleteOldReviewsJob;
    private final Job updateReferenceJob;
    private final Job updateReferenceStatusJob;
    private final Job deleteCertificationsJob;
    private final Job updateResumeViewCountToDbJob;

    public BatchScheduler(JobLauncher jobLauncher, Job deleteOldReviewsJob, Job updateReferenceJob,
            Job deleteCertificationsJob, Job updateResumeViewCountToDbJob) {
        this.jobLauncher = jobLauncher;
        this.deleteOldReviewsJob = deleteOldReviewsJob;
        this.updateReferenceJob = updateReferenceJob;
        this.updateReferenceStatusJob = updateReferenceJob;
        this.deleteCertificationsJob = deleteCertificationsJob;
        this.updateResumeViewCountToDbJob = updateResumeViewCountToDbJob;
    }

    @Scheduled(cron = "0 25 22 * * *")
    public void runDeleteOldReviewsJob() throws JobExecutionException {
        jobLauncher.run(deleteOldReviewsJob, new JobParameters());
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void runUpdateReferenceStatusJob() throws JobExecutionException {
        jobLauncher.run(updateReferenceStatusJob, new JobParameters());
    }

    @Scheduled(cron = "0 30 2 * * *")
    public void runUpdateReferenceJob() throws JobExecutionException {
        jobLauncher.run(updateReferenceJob, new JobParameters());
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void runDeleteCertificationsJob() throws JobExecutionException {
        jobLauncher.run(deleteCertificationsJob, new JobParameters());
    }

    @Scheduled(cron = "0 0 */3 * * *")
    public void runUpdateResumeViewCountToDbJob() throws JobExecutionException {
        jobLauncher.run(updateResumeViewCountToDbJob, new JobParameters());
    }
}
