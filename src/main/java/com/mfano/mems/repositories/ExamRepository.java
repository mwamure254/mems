package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Exam;

public interface ExamRepository extends JpaRepository<Exam, Long>{
    
}
