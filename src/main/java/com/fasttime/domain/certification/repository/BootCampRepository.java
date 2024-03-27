package com.fasttime.domain.certification.repository;

import com.fasttime.domain.certification.entity.BootCamp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BootCampRepository extends JpaRepository<BootCamp, Long> {
    boolean existsByName(String name);
}
