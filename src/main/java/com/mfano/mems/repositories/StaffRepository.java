package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Staff;

public interface StaffRepository extends JpaRepository<Staff, Long>{

    List<Staff> findByReferenceContaining(String key);

    List<Staff> findByBranch(Long b);

    List<Staff> findByBranchAndActive(Long id, boolean b);
    
}
