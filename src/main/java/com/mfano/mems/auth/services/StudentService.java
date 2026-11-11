package com.mfano.mems.auth.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.mfano.mems.models.Student;
import com.mfano.mems.repositories.StudentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class StudentService {
    private final StudentRepository studentRepository;

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public void save(Student student) {
        studentRepository.save(student);
    }

    public void updateBy(Long id) {
        Student existing = findById(id);
        if(existing != null){
            save(existing);
        }
    }

    public void deleteBy(Long id) {
          Student existing = findById(id);
        if(existing != null){
            studentRepository.delete(existing);
        }
    }

    public  Student findById(Long id){
        return studentRepository.findById(id).orElse(null);
    }
    
}
