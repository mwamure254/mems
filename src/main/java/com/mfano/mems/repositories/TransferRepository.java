package com.mfano.mems.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.models.Transfer;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByBranch(Long id);

    List<Transfer> findByBranchAndType(Long id, String filter);
    
}
