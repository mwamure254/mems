package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

    List<Student> findByBranch(Long id);

    List<Student> findByGrade(Long id);

    Integer countByBranchAndGrade(Long branch, Long grade);
    Integer countByBranchAndGradeAndActive(Long branch, Long grade, boolean active);
    Integer countByBranchAndActive(Long branch, boolean active);

    List<Student> findByBranchAndActive(Long id, boolean b);
    
}
