package com.mfano.mems.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Attendance;

public interface AttendanceRepository extends JpaRepository<Attendance , Long>{
    
}
