package com.mfano.mems.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.auth.models.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Long>{
    
}
