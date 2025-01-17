package com.fasttime.global.batch.tasklet;

import com.fasttime.domain.resume.service.ResumeService;
import com.fasttime.domain.resume.service.ViewCountService;
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

    private final ViewCountService viewCountService;
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext)
            throws Exception {
        log.info("자기소개서 조회수 DB로 Update");
        viewCountService.updateViewCountToResume();
        return RepeatStatus.FINISHED;
    }
}
