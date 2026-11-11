package com.mfano.mems.auth.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.auth.models.Term;

public interface TermRepository extends JpaRepository<Term, Long> {
    
}
