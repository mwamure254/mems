package com.mfano.mems.auth.models;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

import com.mfano.mems.config.CustomUserDetails;

@Data
@MappedSuperclass
public abstract class CommonObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String createdBy;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;

    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (createdBy == null) {
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                this.createdBy = userDetails.getEmail();
            } else {
                this.createdBy = "sys";
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
