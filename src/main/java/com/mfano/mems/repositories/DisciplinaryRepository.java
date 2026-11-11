package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Disciplinary;

public interface DisciplinaryRepository extends JpaRepository<Disciplinary , Long>{
    
}
