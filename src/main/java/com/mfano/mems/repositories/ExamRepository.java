package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long>{

    List<Exam> findByNameContaining(String key);

    List<Exam> findByBranch(Long b);
    
}
