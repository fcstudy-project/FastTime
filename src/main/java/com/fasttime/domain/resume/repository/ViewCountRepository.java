package com.fasttime.domain.resume.repository;

import com.fasttime.domain.resume.entity.Resume;
import com.fasttime.domain.resume.entity.ViewCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewCountRepository extends JpaRepository<ViewCount, Long> {

    boolean existsByResumeAndAddress(Resume resume, String address);
}
