package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Marks;

public interface MarkRepository extends JpaRepository<Marks, Long>{
    
}
