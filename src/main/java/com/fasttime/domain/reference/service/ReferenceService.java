package com.fasttime.domain.reference.service;

import com.fasttime.domain.reference.dto.request.ReferenceSearchRequestDto;
import com.fasttime.domain.reference.dto.response.ActivityPageResponseDto;
import com.fasttime.domain.reference.repository.ActivityRepository;
import com.fasttime.domain.reference.service.usecase.ReferenceServiceUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReferenceService implements ReferenceServiceUseCase {

    private final ActivityRepository activityRepository;

    @Override
    @Transactional(readOnly = true)
    public ActivityPageResponseDto searchActivities(
        ReferenceSearchRequestDto searchRequestDto,
        Pageable pageable
    ) {
        return ActivityPageResponseDto.of(activityRepository.findAllBySearchConditions(
            searchRequestDto,
            pageable
        ));
    }
}