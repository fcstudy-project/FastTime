package com.fasttime.domain.bootcamp.repository;

import com.fasttime.domain.bootcamp.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {


}
