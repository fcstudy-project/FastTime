package com.fasttime.domain.bootcamp.repository;

import com.fasttime.domain.bootcamp.entity.Certification;
import com.fasttime.domain.bootcamp.entity.CertificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByMemberId(Long memberId);

    List<Certification> findByStatus(CertificationStatus status);

    @Transactional
    @Modifying
    @Query("DELETE FROM Certification c WHERE c.member.id NOT IN (SELECT m.id FROM Member m)")
    void deleteCertificationsForDeletedMembers();
}
