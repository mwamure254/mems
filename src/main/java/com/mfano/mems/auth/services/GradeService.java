package com.mfano.mems.auth.services;

import java.util.List;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import com.mfano.mems.auth.models.Grade;
import com.mfano.mems.auth.repositories.GradeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class GradeService {
private final GradeRepository gradeRepository;

    public List<Grade> findAll() {
        return gradeRepository.findAll();
    }
    
}
