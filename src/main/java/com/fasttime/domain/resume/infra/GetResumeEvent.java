package com.fasttime.domain.resume.infra;

import com.fasttime.domain.resume.entity.Resume;

public record GetResumeEvent(
    Resume resume,
    String address
) {

}
