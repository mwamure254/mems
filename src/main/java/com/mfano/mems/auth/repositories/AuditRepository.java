package com.mfano.mems.auth.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfano.mems.auth.models.AuditEntry;

public interface AuditRepository extends JpaRepository<AuditEntry, Long> {

    List<AuditEntry> findByAction(String action);

    List<AuditEntry> findByActionContaining(String string);
    
}
