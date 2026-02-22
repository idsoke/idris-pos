package com.warungku.pos.repository;

import com.warungku.pos.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    @Query("SELECT c FROM Category c WHERE c.isActive = true ORDER BY c.sortOrder")
    List<Category> findAllActiveOrdered();
}
