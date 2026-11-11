package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Fees;

public interface FeesRepository extends JpaRepository<Fees, Long>{
    
}
