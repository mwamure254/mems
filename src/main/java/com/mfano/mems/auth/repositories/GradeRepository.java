package com.mfano.mems.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.auth.models.Grade;

public interface GradeRepository extends JpaRepository<Grade, Long>{
    
}
