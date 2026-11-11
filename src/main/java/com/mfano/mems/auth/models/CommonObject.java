package com.mfano.mems.auth.models;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
    private String updatedBy;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean active = false;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true)
    private String reference; // e.g. LN-20261016-0001

    private String description;

    // set Name to Capital first letter
    public void setName(String name) {
        this.name = name.toUpperCase();
    }

    // set description to sentence case
    public void setDescription(String description) {
        if (description == null || description.isBlank()) {
            this.description = description;
            return;
        }

        String value = description.trim().toLowerCase();
        StringBuilder result = new StringBuilder(value.length());
        boolean capitalizeNext = true;

        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);

            if (capitalizeNext && Character.isLetter(c)) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }

            if (c == '.' || c == '!' || c == '?') {
                capitalizeNext = true;
            }
        }

        this.description = result.toString();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();

        if (reference == null) {
            this.reference = String.valueOf(ThreadLocalRandom.current().nextLong(100_000L, 900_000L));
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (createdBy == null) {
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                this.createdBy = userDetails.getUsername();
            } else {
                this.createdBy = "sys";
            }
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (updatedBy == null) {
            if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
                this.updatedBy = userDetails.getUsername();
            } else {
                this.updatedBy = "sys";
            }
        }
    }
}
