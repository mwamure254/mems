package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Borrowing;

public interface BorrowingRepository extends JpaRepository<Borrowing , Long>{
    
}
