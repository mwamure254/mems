package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long>{
    
}
