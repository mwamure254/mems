package com.mfano.mems.auth.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.auth.models.Term;

public interface TermRepository extends JpaRepository<Term, Long> {

    List<Term> findAllByOrderByCreatedAtDesc();
    
}
