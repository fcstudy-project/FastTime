package com.fasttime.domain.certification.repository;

import com.fasttime.domain.certification.entity.Certification;
import com.fasttime.domain.certification.entity.CertificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    List<Certification> findByMemberId(Long memberId);

    Page<Certification> findByStatus(CertificationStatus status, Pageable pageable);

    @Transactional
    @Modifying
    @Query("DELETE FROM Certification c WHERE c.member.id NOT IN (SELECT m.id FROM Member m)")
    void deleteCertificationsForDeletedMembers();
}
