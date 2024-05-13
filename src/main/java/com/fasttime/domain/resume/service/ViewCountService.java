package com.fasttime.domain.resume.service;

import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.entity.ViewCount;
import com.fasttime.domain.resume.repository.ViewCountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ViewCountService {

    private final ViewCountRepository viewCountRepository;

    public void createViewCount(Resume resume, String address) {
        if (viewCountRepository.existsByResumeAndAddress(resume, address)) {
            return;
        }
        ViewCount viewCount = ViewCount.builder()
            .resume(resume)
            .address(address)
            .build();

        viewCountRepository.save(viewCount);
    }


    public void updateViewCountToResume() {
        viewCountRepository.findAll().stream()
            .map(ViewCount::getResume)
            .forEach(Resume::view);

        log.info("[feature] updateViewcount");
        viewCountRepository.deleteAll();
    }
}
