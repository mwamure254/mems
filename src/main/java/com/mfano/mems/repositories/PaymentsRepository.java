package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Payment;

public interface PaymentsRepository extends JpaRepository<Payment, Long>{
    
}
