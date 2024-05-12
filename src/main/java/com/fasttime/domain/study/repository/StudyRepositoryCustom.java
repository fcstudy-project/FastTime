package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.dto.StudyPageRequestService;
import com.fasttime.domain.study.entity.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudyRepositoryCustom {

    Page<Study> search(Pageable pageable);

}
