package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

    List<Category> findByBranch(Long b);

    List<Category> findByNameContaining(String key);
    
}
