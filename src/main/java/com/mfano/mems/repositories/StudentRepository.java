package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {
    
}
