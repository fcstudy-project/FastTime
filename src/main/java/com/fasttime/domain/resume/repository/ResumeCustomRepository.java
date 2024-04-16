package com.fasttime.domain.resume.repository;

import com.fasttime.domain.resume.dto.ResumeQueryResponse;
import com.fasttime.domain.resume.dto.ResumeResponseDto;
import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.entity.Resume;
import java.util.List;
import org.hibernate.query.Page;

public interface ResumeCustomRepository {
    List<Resume> search(ResumesSearchRequest searchCondition);

}
