package com.mfano.mems.auth.repositories;


import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.auth.models.Branch;

public interface BranchRepository extends JpaRepository<Branch, Long> {

    Branch findByName(String name);
}
