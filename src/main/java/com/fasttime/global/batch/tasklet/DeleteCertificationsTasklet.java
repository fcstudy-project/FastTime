package com.fasttime.global.batch.tasklet;

import com.fasttime.domain.certification.repository.CertificationRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.core.step.tasklet.Tasklet;

public class DeleteCertificationsTasklet implements Tasklet {

    private final CertificationRepository certificationRepository;

    public DeleteCertificationsTasklet(CertificationRepository certificationRepository) {
        this.certificationRepository = certificationRepository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        certificationRepository.deleteCertificationsForDeletedMembers();
        return RepeatStatus.FINISHED;
    }
}
