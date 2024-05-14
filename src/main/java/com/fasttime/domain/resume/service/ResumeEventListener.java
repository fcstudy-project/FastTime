package com.fasttime.domain.resume.service;

import com.fasttime.domain.resume.infra.GetResumeEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRES_NEW)
@Component
public class ResumeEventListener {

    private final ViewCountService viewCountService;

    public ResumeEventListener(ViewCountService viewCountService) {
        this.viewCountService = viewCountService;
    }

    @TransactionalEventListener
    public void getResume(GetResumeEvent getResumeEvent){
        viewCountService.createViewCount(getResumeEvent.resume(), getResumeEvent.address());
    }

}
