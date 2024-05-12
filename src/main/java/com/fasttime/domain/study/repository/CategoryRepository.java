package com.fasttime.domain.study.repository;

import com.fasttime.domain.study.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
