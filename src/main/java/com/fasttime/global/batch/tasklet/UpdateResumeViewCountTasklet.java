package com.fasttime.global.batch.tasklet;

import com.fasttime.domain.resume.service.ResumeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateResumeViewCountTasklet implements Tasklet {

    private final ResumeService resumeService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {
        log.info("Redis에 있는 자기소개서 ViewCount DB로 Update");
        resumeService.updateViewCntToDB();

        return RepeatStatus.FINISHED;
    }
}
