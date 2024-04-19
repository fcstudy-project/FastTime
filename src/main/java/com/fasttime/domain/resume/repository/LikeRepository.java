package com.fasttime.domain.resume.repository;

import com.fasttime.domain.member.entity.Member;
import com.fasttime.domain.resume.entity.Like;
import com.fasttime.domain.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByMemberAndResume(Member member, Resume resume);
}
