package com.mfano.mems.models;

import com.mfano.mems.auth.models.CommonObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "announcements")
@Setter
@Getter
public class Announcement extends CommonObject {
    private String upload;
    
    @Column(name = "role_id")
    private Long audience;

    @Column(name = "branch_id")
    private Long branch;
}
