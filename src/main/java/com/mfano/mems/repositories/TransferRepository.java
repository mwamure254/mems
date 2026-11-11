package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {
    
}
