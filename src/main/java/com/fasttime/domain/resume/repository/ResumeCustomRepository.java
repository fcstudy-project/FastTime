package com.fasttime.domain.resume.repository;

import com.fasttime.domain.resume.dto.ResumesSearchRequest;
import com.fasttime.domain.resume.entity.Resume;
import java.util.List;

public interface ResumeCustomRepository {

    List<Resume> search(ResumesSearchRequest searchCondition);

}
