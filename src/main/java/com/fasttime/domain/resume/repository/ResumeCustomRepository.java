package com.fasttime.domain.resume.repository;

import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.entity.Resume;
import java.util.List;

public interface ResumeCustomRepository {

    List<Resume> search(ResumesSearchRequest searchCondition);
    void addViewCountFromRedis(Long resumeId, Long viewCount);

    Long getLikeCount(Long resumeId);

    List<Resume> getResumesCreatedWithinTwoWeeks();

    List<Resume> getRecentResumesBySizeExceptIds(int size, List<Long> ids);
}
