package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Marks;

public interface MarkRepository extends JpaRepository<Marks, Long>{

    List<Marks> findByStudentAndExam(Long student, Long exam);

    List<Marks> findByExamAndSubject(Long exam, Long subject);

    Marks findByStudentAndExamAndSubject(Long student, Long exam, Long subject);
    
}
